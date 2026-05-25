package com.venkat.healthapp.food.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Entities ──────────────────────────────────────────────────────────────────
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,       // Fruits, Nuts, Grains, Vegetables, Dairy, Protein, Custom
    val unit: String,           // "100g", "1 piece", "1 cup"
    val calories: Float,
    val protein: Float,         // grams
    val carbs: Float,
    val fat: Float,
    val fiber: Float,
    val isCustom: Boolean = false
)

@Entity(tableName = "food_logs")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,           // "2026-05-18"
    val foodItemId: Int,
    val foodName: String,
    val quantity: Float,        // multiplier of base unit
    val unit: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float,
    val mealType: String,       // Breakfast, Lunch, Dinner, Snack
    val loggedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Venkatramana",
    val weightKg: Float = 0f,
    val heightCm: Float = 0f,
    val age: Int = 26,
    val gender: String = "Male",
    val activityLevel: String = "Moderate", // Sedentary, Light, Moderate, Active, VeryActive
    val goal: String = "Aesthetic",         // Lose, Maintain, Gain, Aesthetic
    val targetWeightKg: Float = 0f
)

// ── DAOs ──────────────────────────────────────────────────────────────────────
@Dao interface FoodItemDao {
    @Query("SELECT * FROM food_items ORDER BY category, name") fun allItems(): Flow<List<FoodItem>>
    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :q || '%' ORDER BY name LIMIT 20")
    fun search(q: String): Flow<List<FoodItem>>
    @Query("SELECT * FROM food_items WHERE category=:cat ORDER BY name") fun byCategory(cat: String): Flow<List<FoodItem>>
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertAll(items: List<FoodItem>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: FoodItem): Long
    @Delete suspend fun delete(item: FoodItem)
    @Query("SELECT COUNT(*) FROM food_items") suspend fun count(): Int
}

@Dao interface FoodLogDao {
    @Query("SELECT * FROM food_logs WHERE date=:date ORDER BY loggedAt") fun logsForDate(date: String): Flow<List<FoodLog>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(log: FoodLog)
    @Delete suspend fun delete(log: FoodLog)
    @Query("SELECT DISTINCT date FROM food_logs ORDER BY date DESC") fun allDates(): Flow<List<String>>
}

@Dao interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id=1") fun get(): Flow<UserProfile?>
    @Upsert suspend fun upsert(profile: UserProfile)
}

// ── Indian nutrition database (per 100g unless noted) ────────────────────────
fun indianFoodDatabase(): List<FoodItem> = listOf(
    // ── FRUITS ────────────────────────────────────────────────────────────────
    FoodItem(name="Banana",          category="Fruits", unit="1 medium (120g)", calories=89f,  protein=1.1f,  carbs=23f,  fat=0.3f, fiber=2.6f),
    FoodItem(name="Apple",           category="Fruits", unit="1 medium (180g)", calories=95f,  protein=0.5f,  carbs=25f,  fat=0.3f, fiber=4.4f),
    FoodItem(name="Mango",           category="Fruits", unit="1 cup (165g)",    calories=99f,  protein=1.4f,  carbs=25f,  fat=0.6f, fiber=2.6f),
    FoodItem(name="Papaya",          category="Fruits", unit="1 cup (145g)",    calories=62f,  protein=0.7f,  carbs=16f,  fat=0.4f, fiber=2.5f),
    FoodItem(name="Guava",           category="Fruits", unit="1 medium (90g)",  calories=68f,  protein=2.6f,  carbs=14f,  fat=1.0f, fiber=5.4f),
    FoodItem(name="Pomegranate",     category="Fruits", unit="100g seeds",      calories=83f,  protein=1.7f,  carbs=19f,  fat=1.2f, fiber=4.0f),
    FoodItem(name="Watermelon",      category="Fruits", unit="1 cup (152g)",    calories=46f,  protein=0.9f,  carbs=11f,  fat=0.2f, fiber=0.6f),
    FoodItem(name="Grapes",          category="Fruits", unit="1 cup (92g)",     calories=62f,  protein=0.6f,  carbs=16f,  fat=0.3f, fiber=0.8f),
    FoodItem(name="Orange",          category="Fruits", unit="1 medium (130g)", calories=62f,  protein=1.2f,  carbs=15f,  fat=0.2f, fiber=3.1f),
    FoodItem(name="Pineapple",       category="Fruits", unit="1 cup (165g)",    calories=82f,  protein=0.9f,  carbs=22f,  fat=0.2f, fiber=2.3f),
    // ── NUTS & SEEDS ──────────────────────────────────────────────────────────
    FoodItem(name="Almonds",         category="Nuts",   unit="1 handful (30g)", calories=173f, protein=6.0f,  carbs=6.0f, fat=15f,  fiber=3.5f),
    FoodItem(name="Cashews",         category="Nuts",   unit="1 handful (30g)", calories=157f, protein=5.1f,  carbs=9.0f, fat=12f,  fiber=0.9f),
    FoodItem(name="Walnuts",         category="Nuts",   unit="1 handful (30g)", calories=196f, protein=4.6f,  carbs=4.1f, fat=20f,  fiber=2.0f),
    FoodItem(name="Peanuts",         category="Nuts",   unit="1 handful (30g)", calories=161f, protein=7.3f,  carbs=4.6f, fat=14f,  fiber=2.4f),
    FoodItem(name="Pistachios",      category="Nuts",   unit="1 handful (30g)", calories=159f, protein=6.0f,  carbs=8.0f, fat=13f,  fiber=3.0f),
    FoodItem(name="Flaxseeds",       category="Nuts",   unit="1 tbsp (10g)",    calories=55f,  protein=1.9f,  carbs=3.0f, fat=4.3f, fiber=2.8f),
    FoodItem(name="Chia Seeds",      category="Nuts",   unit="1 tbsp (10g)",    calories=49f,  protein=1.7f,  carbs=4.2f, fat=3.1f, fiber=3.4f),
    FoodItem(name="Pumpkin Seeds",   category="Nuts",   unit="1 handful (30g)", calories=163f, protein=8.5f,  carbs=5.0f, fat=14f,  fiber=1.7f),
    FoodItem(name="Sunflower Seeds", category="Nuts",   unit="1 handful (30g)", calories=174f, protein=6.1f,  carbs=6.0f, fat=15f,  fiber=2.4f),
    // ── GRAINS & CEREALS ──────────────────────────────────────────────────────
    FoodItem(name="White Rice",      category="Grains", unit="1 cup cooked (186g)", calories=242f, protein=4.4f, carbs=53f,  fat=0.4f, fiber=0.6f),
    FoodItem(name="Brown Rice",      category="Grains", unit="1 cup cooked (195g)", calories=216f, protein=5.0f, carbs=45f,  fat=1.8f, fiber=3.5f),
    FoodItem(name="Roti/Chapati",    category="Grains", unit="1 piece (40g)",   calories=120f, protein=3.5f,  carbs=22f,  fat=2.5f, fiber=2.2f),
    FoodItem(name="Idli",            category="Grains", unit="2 pieces (80g)",  calories=78f,  protein=2.1f,  carbs=15f,  fat=0.5f, fiber=0.9f),
    FoodItem(name="Dosa",            category="Grains", unit="1 medium (100g)", calories=133f, protein=3.5f,  carbs=22f,  fat=3.7f, fiber=1.2f),
    FoodItem(name="Oats",            category="Grains", unit="1 cup cooked (234g)", calories=166f, protein=5.9f, carbs=28f, fat=3.6f, fiber=4.0f),
    FoodItem(name="Poha",            category="Grains", unit="1 cup (244g)",    calories=244f, protein=4.1f,  carbs=49f,  fat=3.2f, fiber=1.8f),
    FoodItem(name="Upma",            category="Grains", unit="1 cup (200g)",    calories=196f, protein=4.5f,  carbs=30f,  fat=7.0f, fiber=2.0f),
    FoodItem(name="Wheat Bread",     category="Grains", unit="1 slice (30g)",   calories=79f,  protein=2.7f,  carbs=15f,  fat=1.0f, fiber=1.2f),
    // ── VEGETABLES ────────────────────────────────────────────────────────────
    FoodItem(name="Spinach",         category="Vegetables", unit="1 cup (30g)", calories=7f,   protein=0.9f,  carbs=1.1f, fat=0.1f, fiber=0.7f),
    FoodItem(name="Tomato",          category="Vegetables", unit="1 medium (123g)", calories=22f, protein=1.1f, carbs=4.8f, fat=0.2f, fiber=1.5f),
    FoodItem(name="Carrot",          category="Vegetables", unit="1 medium (61g)",  calories=25f, protein=0.6f, carbs=6.0f, fat=0.1f, fiber=1.7f),
    FoodItem(name="Brinjal",         category="Vegetables", unit="1 cup (82g)",     calories=20f, protein=0.8f, carbs=4.8f, fat=0.2f, fiber=2.5f),
    FoodItem(name="Bitter Gourd",    category="Vegetables", unit="1 cup (94g)",     calories=20f, protein=1.0f, carbs=4.3f, fat=0.2f, fiber=2.8f),
    FoodItem(name="Drumstick",       category="Vegetables", unit="100g",            calories=26f, protein=2.1f, carbs=3.7f, fat=0.2f, fiber=2.0f),
    FoodItem(name="Green Beans",     category="Vegetables", unit="1 cup (110g)",    calories=35f, protein=1.8f, carbs=7.8f, fat=0.1f, fiber=3.4f),
    FoodItem(name="Cauliflower",     category="Vegetables", unit="1 cup (100g)",    calories=25f, protein=2.0f, carbs=5.0f, fat=0.3f, fiber=2.0f),
    // ── DAIRY & PROTEIN ───────────────────────────────────────────────────────
    FoodItem(name="Egg (whole)",     category="Protein", unit="1 egg (50g)",     calories=72f,  protein=6.3f,  carbs=0.4f, fat=5.0f, fiber=0f),
    FoodItem(name="Egg White",       category="Protein", unit="1 white (33g)",   calories=17f,  protein=3.6f,  carbs=0.2f, fat=0.1f, fiber=0f),
    FoodItem(name="Chicken Breast",  category="Protein", unit="100g cooked",     calories=165f, protein=31f,   carbs=0f,   fat=3.6f, fiber=0f),
    FoodItem(name="Toor Dal",        category="Protein", unit="1 cup cooked (198g)", calories=198f, protein=13f, carbs=34f, fat=0.7f, fiber=6.4f),
    FoodItem(name="Moong Dal",       category="Protein", unit="1 cup cooked (202g)", calories=212f, protein=14f, carbs=38f, fat=0.8f, fiber=7.6f),
    FoodItem(name="Chana Dal",       category="Protein", unit="1 cup cooked (164g)", calories=269f, protein=14f, carbs=45f, fat=4.5f, fiber=12f),
    FoodItem(name="Rajma",           category="Protein", unit="1 cup cooked (177g)", calories=225f, protein=15f, carbs=40f, fat=0.9f, fiber=13f),
    FoodItem(name="Paneer",          category="Dairy",   unit="100g",            calories=265f, protein=18f,   carbs=3.6f, fat=20f,  fiber=0f),
    FoodItem(name="Curd/Yogurt",     category="Dairy",   unit="1 cup (245g)",    calories=150f, protein=8.5f,  carbs=17f,  fat=3.8f, fiber=0f),
    FoodItem(name="Milk (whole)",    category="Dairy",   unit="1 glass (240ml)", calories=149f, protein=8.0f,  carbs=12f,  fat=8.0f, fiber=0f),
    FoodItem(name="Whey Protein",    category="Protein", unit="1 scoop (30g)",   calories=120f, protein=24f,   carbs=3.0f, fat=1.5f, fiber=0f),
    // ── INDIAN SNACKS ─────────────────────────────────────────────────────────
    FoodItem(name="Samosa",          category="Snacks",  unit="1 piece (100g)",  calories=262f, protein=4.5f,  carbs=31f,  fat=13f,  fiber=2.0f),
    FoodItem(name="Vada",            category="Snacks",  unit="1 piece (50g)",   calories=147f, protein=4.0f,  carbs=14f,  fat=8.5f, fiber=1.5f),
    FoodItem(name="Peanut Chikki",   category="Snacks",  unit="1 piece (25g)",   calories=116f, protein=3.0f,  carbs=14f,  fat=5.8f, fiber=1.0f),
)

// ── Nutrition target calculator ───────────────────────────────────────────────
data class NutritionTargets(
    val calories: Int, val protein: Int, val carbs: Int, val fat: Int,
    val fiber: Int, val water: Float, val targetWeight: Float,
    val bmi: Float, val bmr: Float, val summary: String
)

fun calculateTargets(profile: UserProfile): NutritionTargets {
    val w = profile.weightKg; val h = profile.heightCm / 100f; val a = profile.age
    if (w <= 0f || h <= 0f) return NutritionTargets(2000,150,250,65,30,3f,w,0f,0f,"Set your profile first")

    val bmi   = w / (h * h)
    val bmr   = if (profile.gender == "Male")
        (10 * w + 6.25 * (h * 100) - 5 * a + 5).toFloat()
    else
        (10 * w + 6.25 * (h * 100) - 5 * a - 161).toFloat()

    val tdee  = bmr * when (profile.activityLevel) {
        "Sedentary"  -> 1.2f; "Light" -> 1.375f; "Moderate" -> 1.55f
        "Active"     -> 1.725f; else  -> 1.9f
    }

    // Aesthetic target weight (BMI ~22 for men, 21 for women)
    val idealBmi   = if (profile.gender == "Male") 22f else 21f
    val targetWeight = idealBmi * h * h

    val (calories, summary) = when (profile.goal) {
        "Lose"      -> Pair((tdee - 400).toInt(), "Calorie deficit for fat loss")
        "Gain"      -> Pair((tdee + 300).toInt(), "Calorie surplus for muscle gain")
        "Aesthetic" -> Pair(tdee.toInt(), "Maintenance for body recomposition")
        else        -> Pair(tdee.toInt(), "Maintenance calories")
    }

    val protein = (w * 2.0f).toInt()   // 2g per kg for aesthetic
    val fat     = (calories * 0.25f / 9).toInt()
    val carbs   = ((calories - protein * 4 - fat * 9) / 4).toInt()
    val fiber   = if (profile.gender == "Male") 38 else 25
    val water   = (w * 0.033f).coerceAtLeast(2.5f)

    return NutritionTargets(calories, protein, carbs.coerceAtLeast(0), fat, fiber, water, targetWeight, bmi, bmr, summary)
}
