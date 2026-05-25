package com.venkat.healthapp.vault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import android.util.Base64

// ── Vault categories ──────────────────────────────────────────────────────────
enum class VaultCategory(val emoji: String, val label: String) {
    BANK("🏦", "Bank Account"),
    CARD("💳", "Credit / Debit Card"),
    UPI("📱", "UPI & Wallets"),
    EMAIL("📧", "Email Account"),
    SOCIAL("📸", "Social Media"),
    APP("📲", "App / Website"),
    WIFI("📶", "WiFi Password"),
    SYSTEM("💻", "System / Device"),
    GOVERNMENT("🪪", "Government ID"),
    INSURANCE("🛡", "Insurance"),
    OTHER("🔐", "Other")
}

// ── Entity ────────────────────────────────────────────────────────────────────
@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val title: String,              // e.g. "SBI Savings Account"
    val username: String = "",      // account number / username / email
    val encryptedPassword: String = "", // AES encrypted
    val encryptedExtra: String = "",    // customer ID, IFSC, PIN etc — encrypted
    val note: String = "",          // plain text note
    val website: String = "",       // optional URL
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ── DAO ───────────────────────────────────────────────────────────────────────
@Dao
interface VaultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VaultItem): Long

    @Update
    suspend fun update(item: VaultItem)

    @Delete
    suspend fun delete(item: VaultItem)

    @Query("SELECT * FROM vault_items ORDER BY isFavorite DESC, title ASC")
    fun allItems(): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE category = :cat ORDER BY title ASC")
    fun byCategory(cat: String): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE title LIKE '%' || :q || '%' OR username LIKE '%' || :q || '%' ORDER BY title")
    fun search(q: String): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE isFavorite = 1 ORDER BY title")
    fun favorites(): Flow<List<VaultItem>>

    @Query("SELECT COUNT(*) FROM vault_items")
    suspend fun count(): Int

    @Query("SELECT * FROM vault_items WHERE id = :id")
    suspend fun getById(id: Int): VaultItem?
}

// ── AES Encryption using Android Keystore ────────────────────────────────────
object VaultEncryption {

    private const val KEY_ALIAS    = "health_app_vault_key"
    private const val KEYSTORE     = "AndroidKeyStore"
    private const val ALGORITHM    = "AES/GCM/NoPadding"
    private const val KEY_SIZE     = 256
    private const val GCM_TAG_LEN  = 128

    // Generate or get key from Android Keystore
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).also { it.load(null) }
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        keyGen.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE)
                .build()
        )
        return keyGen.generateKey()
    }

    fun encrypt(plainText: String): String {
        if (plainText.isBlank()) return ""
        return try {
            val key    = getOrCreateKey()
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv         = cipher.iv
            val encrypted  = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            // Store iv + encrypted as base64
            val combined   = iv + encrypted
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            Base64.encodeToString(plainText.toByteArray(), Base64.DEFAULT)
        }
    }

    fun decrypt(encryptedText: String): String {
        if (encryptedText.isBlank()) return ""
        return try {
            val key      = getOrCreateKey()
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)
            val iv       = combined.copyOfRange(0, 12)
            val data     = combined.copyOfRange(12, combined.size)
            val cipher   = Cipher.getInstance(ALGORITHM)
            val spec     = GCMParameterSpec(GCM_TAG_LEN, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            String(cipher.doFinal(data), Charsets.UTF_8)
        } catch (e: Exception) {
            try { String(Base64.decode(encryptedText, Base64.DEFAULT)) } catch (e2: Exception) { "" }
        }
    }
}

// ── Master PIN manager ────────────────────────────────────────────────────────
object VaultPinManager {

    private const val PREFS_NAME     = "vault_security"
    private const val KEY_PIN_HASH   = "pin_hash"
    private const val KEY_HINT       = "pin_hint"
    private const val KEY_RECOVERY   = "recovery_answer_hash"
    private const val KEY_SETUP_DONE = "setup_done"
    private const val KEY_ATTEMPTS   = "failed_attempts"
    private const val KEY_LOCKED_AT  = "locked_at"
    private const val MAX_ATTEMPTS   = 5
    private const val LOCK_DURATION  = 5 * 60 * 1000L  // 5 minutes

    fun isSetupDone(context: android.content.Context): Boolean {
        return prefs(context).getBoolean(KEY_SETUP_DONE, false)
    }

    fun setupPin(
        context: android.content.Context,
        pin: String,
        hint: String,
        recoveryAnswer: String
    ) {
        prefs(context).edit()
            .putString(KEY_PIN_HASH,   hashPin(pin))
            .putString(KEY_HINT,       hint)
            .putString(KEY_RECOVERY,   hashPin(recoveryAnswer.lowercase().trim()))
            .putBoolean(KEY_SETUP_DONE, true)
            .putInt(KEY_ATTEMPTS, 0)
            .apply()
    }

    fun verifyPin(context: android.content.Context, pin: String): PinResult {
        val p = prefs(context)

        // Check lockout
        val lockedAt = p.getLong(KEY_LOCKED_AT, 0L)
        if (lockedAt > 0) {
            val elapsed = System.currentTimeMillis() - lockedAt
            if (elapsed < LOCK_DURATION) {
                val remaining = ((LOCK_DURATION - elapsed) / 1000 / 60).toInt() + 1
                return PinResult.LOCKED(remaining)
            } else {
                // Unlock
                p.edit().putLong(KEY_LOCKED_AT, 0L).putInt(KEY_ATTEMPTS, 0).apply()
            }
        }

        val stored   = p.getString(KEY_PIN_HASH, "") ?: ""
        val attempts = p.getInt(KEY_ATTEMPTS, 0)

        return if (hashPin(pin) == stored) {
            p.edit().putInt(KEY_ATTEMPTS, 0).apply()
            PinResult.SUCCESS
        } else {
            val newAttempts = attempts + 1
            if (newAttempts >= MAX_ATTEMPTS) {
                p.edit()
                    .putInt(KEY_ATTEMPTS, newAttempts)
                    .putLong(KEY_LOCKED_AT, System.currentTimeMillis())
                    .apply()
                PinResult.LOCKED(5)
            } else {
                p.edit().putInt(KEY_ATTEMPTS, newAttempts).apply()
                PinResult.WRONG(MAX_ATTEMPTS - newAttempts)
            }
        }
    }

    fun verifyRecovery(context: android.content.Context, answer: String): Boolean {
        val stored = prefs(context).getString(KEY_RECOVERY, "") ?: ""
        return hashPin(answer.lowercase().trim()) == stored
    }

    fun resetPin(context: android.content.Context, newPin: String) {
        prefs(context).edit()
            .putString(KEY_PIN_HASH, hashPin(newPin))
            .putInt(KEY_ATTEMPTS, 0)
            .putLong(KEY_LOCKED_AT, 0L)
            .apply()
    }

    fun getHint(context: android.content.Context): String {
        return prefs(context).getString(KEY_HINT, "") ?: ""
    }

    fun changePin(
        context: android.content.Context,
        oldPin: String,
        newPin: String,
        newHint: String
    ): Boolean {
        val result = verifyPin(context, oldPin)
        if (result != PinResult.SUCCESS) return false
        prefs(context).edit()
            .putString(KEY_PIN_HASH, hashPin(newPin))
            .putString(KEY_HINT, newHint)
            .apply()
        return true
    }

    private fun hashPin(pin: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash   = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.DEFAULT)
    }

    private fun prefs(context: android.content.Context) =
        context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
}

sealed class PinResult {
    object SUCCESS : PinResult()
    data class WRONG(val attemptsLeft: Int) : PinResult()
    data class LOCKED(val minutesLeft: Int) : PinResult()
}