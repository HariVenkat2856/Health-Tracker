package com.venkat.healthapp.expense.receipt

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object OcrProcessor {

    // ── Run ML Kit OCR on bitmap ──────────────────────────────────────────────
    suspend fun processImage(bitmap: Bitmap): OcrResult =
        suspendCancellableCoroutine { cont ->
            val image      = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val rawText = visionText.text
                    cont.resume(parseOcrText(rawText))
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }

    // ── Parse extracted text for amounts, dates, merchant ────────────────────
    fun parseOcrText(text: String): OcrResult {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }

        return OcrResult(
            rawText          = text,
            detectedAmount   = detectAmount(lines),
            detectedDate     = detectDate(lines),
            detectedMerchant = detectMerchant(lines)
        )
    }

    // ── Amount detection — finds largest currency amount ──────────────────────
    private fun detectAmount(lines: List<String>): Float {
        val amountPatterns = listOf(
            // ₹ symbol patterns
            Regex("""₹\s*([0-9,]+\.?[0-9]*)"""),
            Regex("""Rs\.?\s*([0-9,]+\.?[0-9]*)"""),
            Regex("""INR\s*([0-9,]+\.?[0-9]*)"""),
            // Total keywords
            Regex("""(?:total|grand total|amount|net amount|payable|to pay|bill amount)[:\s]*(?:₹|rs\.?|inr)?\s*([0-9,]+\.?[0-9]*)""", RegexOption.IGNORE_CASE),
            // Plain number with decimals (last resort)
            Regex("""([0-9,]+\.[0-9]{2})""")
        )

        val candidates = mutableListOf<Float>()

        lines.forEach { line ->
            amountPatterns.forEach { pattern ->
                pattern.findAll(line).forEach { match ->
                    val numStr = match.groupValues.last()
                        .replace(",", "")
                        .trim()
                    numStr.toFloatOrNull()?.let {
                        if (it > 0 && it < 1000000) candidates.add(it)
                    }
                }
            }
        }

        // Prefer amounts near "total" keywords
        val totalLineAmount = lines.firstOrNull { line ->
            line.contains(Regex("total|grand total|payable|to pay", RegexOption.IGNORE_CASE))
        }?.let { line ->
            Regex("""([0-9,]+\.?[0-9]*)""").findAll(line)
                .mapNotNull { it.value.replace(",", "").toFloatOrNull() }
                .maxOrNull()
        }

        return totalLineAmount ?: candidates.maxOrNull() ?: 0f
    }

    // ── Date detection ────────────────────────────────────────────────────────
    private fun detectDate(lines: List<String>): String {
        val datePatterns = listOf(
            // DD/MM/YYYY or DD-MM-YYYY
            Regex("""(\d{1,2}[\/\-\.]\d{1,2}[\/\-\.]\d{2,4})"""),
            // DD MMM YYYY
            Regex("""(\d{1,2}\s+(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*\.?\s+\d{2,4})""", RegexOption.IGNORE_CASE),
            // YYYY-MM-DD
            Regex("""(\d{4}[\/\-]\d{2}[\/\-]\d{2})""")
        )

        lines.forEach { line ->
            datePatterns.forEach { pattern ->
                pattern.find(line)?.let { return it.value }
            }
        }
        return ""
    }

    // ── Merchant name detection ───────────────────────────────────────────────
    private fun detectMerchant(lines: List<String>): String {
        // Usually merchant name is in first 1-3 lines, all caps, no numbers
        val merchantCandidates = lines.take(5).filter { line ->
            line.length > 3 &&
                    line.length < 60 &&
                    !line.any { it.isDigit() } &&
                    line.any { it.isLetter() }
        }

        // Skip lines that look like addresses or generic words
        val skipWords = listOf("receipt", "invoice", "bill", "tax", "gst",
            "thank", "welcome", "phone", "mobile", "email", "www")

        val merchant = merchantCandidates.firstOrNull { line ->
            skipWords.none { line.lowercase().contains(it) }
        }

        return merchant?.trim() ?: ""
    }
}