package com.venkat.healthapp.workout.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Entities ──────────────────────────────────────────────────────────────────
@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val splitName: String,        // "Push", "Pull", "Legs", "Rest"
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Float = 0f,
    val durationMinutes: Int = 0,
    val notes: String = "",
    val completed: Boolean = false,
    val loggedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_progress")
data class WorkoutProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val splitCompleted: String,
    val totalExercises: Int,
    val completedExercises: Int,
    val durationMinutes: Int,
    val caloriesBurned: Int
)

// ── DAOs ──────────────────────────────────────────────────────────────────────
@Dao
interface WorkoutLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WorkoutLog): Long

    @Delete
    suspend fun delete(log: WorkoutLog)

    @Query("SELECT * FROM workout_logs WHERE date = :date ORDER BY loggedAt")
    fun logsForDate(date: String): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs ORDER BY loggedAt DESC")
    fun allLogs(): Flow<List<WorkoutLog>>

    @Query("SELECT DISTINCT date FROM workout_logs ORDER BY date DESC")
    fun allDates(): Flow<List<String>>

    @Query("SELECT COUNT(DISTINCT date) FROM workout_logs WHERE completed = 1")
    fun totalWorkoutDays(): Flow<Int>

    @Query("SELECT * FROM workout_logs WHERE date = :date AND completed = 1")
    fun completedForDate(date: String): Flow<List<WorkoutLog>>
}

@Dao
interface WorkoutProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(p: WorkoutProgress)

    @Query("SELECT * FROM workout_progress ORDER BY date DESC")
    fun allProgress(): Flow<List<WorkoutProgress>>

    @Query("SELECT * FROM workout_progress ORDER BY date DESC LIMIT 7")
    fun lastSevenDays(): Flow<List<WorkoutProgress>>
}

// ── Exercise model ────────────────────────────────────────────────────────────
data class Exercise(
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: String,          // "8-12" or "12-15" or "To failure"
    val restSeconds: Int,
    val gifRes: String,        // URL for gif
    val instructions: List<String>,
    val difficulty: String,    // Beginner, Intermediate, Advanced
    val equipment: String,     // Bodyweight, Dumbbell, Barbell, Cable, Machine
    val calories: Int          // approx per set
)

data class WorkoutSplit(
    val name: String,
    val emoji: String,
    val dayLabel: String,      // "Day 1", "Day 2" etc
    val musclesFocus: String,
    val exercises: List<Exercise>,
    val estimatedMinutes: Int,
    val difficulty: String
)

// ── PPL Split (Push Pull Legs) — best for aesthetic body ─────────────────────
val PPL_SPLIT = listOf(

    WorkoutSplit(
        name = "Push",
        emoji = "💪",
        dayLabel = "Day 1 & 4",
        musclesFocus = "Chest • Shoulders • Triceps",
        estimatedMinutes = 60,
        difficulty = "Intermediate",
        exercises = listOf(
            Exercise(
                name = "Push Ups",
                muscleGroup = "Chest",
                sets = 4, reps = "12-15", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/l4FGrYKtP0pBGpBAA/giphy.gif",
                instructions = listOf(
                    "Place hands shoulder-width apart",
                    "Keep body straight like a plank",
                    "Lower chest to floor, elbows at 45°",
                    "Push back up fully extending arms"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 8
            ),
            Exercise(
                name = "Dumbbell Bench Press",
                muscleGroup = "Chest",
                sets = 4, reps = "8-12", restSeconds = 90,
                gifRes = "https://media.giphy.com/media/3o7TKSjRrfIPjeiVyM/giphy.gif",
                instructions = listOf(
                    "Lie flat on bench, dumbbells at chest level",
                    "Press dumbbells up until arms are straight",
                    "Slowly lower back to chest level",
                    "Keep wrists straight throughout"
                ),
                difficulty = "Intermediate", equipment = "Dumbbell", calories = 12
            ),
            Exercise(
                name = "Incline Dumbbell Press",
                muscleGroup = "Upper Chest",
                sets = 3, reps = "10-12", restSeconds = 90,
                gifRes = "https://media.giphy.com/media/xT9IgzoKnwFNmISR8I/giphy.gif",
                instructions = listOf(
                    "Set bench to 30-45 degree incline",
                    "Hold dumbbells at shoulder level",
                    "Press up and slightly inward",
                    "Lower slowly under control"
                ),
                difficulty = "Intermediate", equipment = "Dumbbell", calories = 11
            ),
            Exercise(
                name = "Dumbbell Lateral Raise",
                muscleGroup = "Shoulders",
                sets = 4, reps = "12-15", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/3o7TKF1fSIs1R19B8k/giphy.gif",
                instructions = listOf(
                    "Stand with dumbbells at sides",
                    "Raise arms to shoulder height",
                    "Keep slight bend in elbows",
                    "Lower slowly — don't swing"
                ),
                difficulty = "Beginner", equipment = "Dumbbell", calories = 6
            ),
            Exercise(
                name = "Overhead Press",
                muscleGroup = "Shoulders",
                sets = 3, reps = "8-12", restSeconds = 90,
                gifRes = "https://media.giphy.com/media/l4FGDsnGWuHYKHMBy/giphy.gif",
                instructions = listOf(
                    "Stand with dumbbells at shoulder height",
                    "Press straight up overhead",
                    "Don't arch lower back",
                    "Lower back to shoulders slowly"
                ),
                difficulty = "Intermediate", equipment = "Dumbbell", calories = 10
            ),
            Exercise(
                name = "Tricep Dips",
                muscleGroup = "Triceps",
                sets = 3, reps = "12-15", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/3o7TKSx0g7RqRnVqQg/giphy.gif",
                instructions = listOf(
                    "Place hands on chair/bench behind you",
                    "Lower body by bending elbows",
                    "Keep elbows close to body",
                    "Push back up to start"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 9
            ),
            Exercise(
                name = "Tricep Overhead Extension",
                muscleGroup = "Triceps",
                sets = 3, reps = "12-15", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/3o7TKRwpns23QMNNiU/giphy.gif",
                instructions = listOf(
                    "Hold one dumbbell with both hands overhead",
                    "Lower behind head by bending elbows",
                    "Extend arms back up fully",
                    "Keep upper arms close to head"
                ),
                difficulty = "Beginner", equipment = "Dumbbell", calories = 7
            )
        )
    ),

    WorkoutSplit(
        name = "Pull",
        emoji = "🏋️",
        dayLabel = "Day 2 & 5",
        musclesFocus = "Back • Biceps • Rear Delts",
        estimatedMinutes = 60,
        difficulty = "Intermediate",
        exercises = listOf(
            Exercise(
                name = "Pull Ups / Chin Ups",
                muscleGroup = "Back",
                sets = 4, reps = "6-10", restSeconds = 90,
                gifRes = "https://media.giphy.com/media/3o7TKMt4aBz7G3wVWM/giphy.gif",
                instructions = listOf(
                    "Hang from bar with hands shoulder-width",
                    "Pull chest up to bar level",
                    "Squeeze shoulder blades together",
                    "Lower slowly — full hang at bottom"
                ),
                difficulty = "Intermediate", equipment = "Bodyweight", calories = 15
            ),
            Exercise(
                name = "Dumbbell Row",
                muscleGroup = "Back",
                sets = 4, reps = "10-12", restSeconds = 75,
                gifRes = "https://media.giphy.com/media/3o7TKRwpns23QMNNiU/giphy.gif",
                instructions = listOf(
                    "Place knee and hand on bench for support",
                    "Hold dumbbell, arm extended down",
                    "Pull elbow up past your hip",
                    "Squeeze back at top, lower slowly"
                ),
                difficulty = "Beginner", equipment = "Dumbbell", calories = 11
            ),
            Exercise(
                name = "Face Pulls",
                muscleGroup = "Rear Delts",
                sets = 3, reps = "15-20", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/xT9IgzoKnwFNmISR8I/giphy.gif",
                instructions = listOf(
                    "Use resistance band or cable at face height",
                    "Pull toward face with elbows high",
                    "Externally rotate at end of movement",
                    "Control the return movement"
                ),
                difficulty = "Beginner", equipment = "Cable", calories = 6
            ),
            Exercise(
                name = "Dumbbell Curl",
                muscleGroup = "Biceps",
                sets = 4, reps = "10-12", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/3o7TKF1fSIs1R19B8k/giphy.gif",
                instructions = listOf(
                    "Stand with dumbbells at sides",
                    "Curl up keeping elbows fixed",
                    "Squeeze bicep at top",
                    "Lower slowly — full extension"
                ),
                difficulty = "Beginner", equipment = "Dumbbell", calories = 7
            ),
            Exercise(
                name = "Hammer Curl",
                muscleGroup = "Biceps & Forearms",
                sets = 3, reps = "10-12", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/l4FGrYKtP0pBGpBAA/giphy.gif",
                instructions = listOf(
                    "Hold dumbbells with neutral grip (thumbs up)",
                    "Curl up keeping wrists neutral",
                    "Don't swing body for momentum",
                    "Lower with control"
                ),
                difficulty = "Beginner", equipment = "Dumbbell", calories = 7
            ),
            Exercise(
                name = "Reverse Fly",
                muscleGroup = "Rear Delts",
                sets = 3, reps = "12-15", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/3o7TKSjRrfIPjeiVyM/giphy.gif",
                instructions = listOf(
                    "Bend forward 45 degrees, dumbbells hanging",
                    "Raise arms out to sides",
                    "Squeeze rear delts at top",
                    "Lower under control"
                ),
                difficulty = "Beginner", equipment = "Dumbbell", calories = 6
            )
        )
    ),

    WorkoutSplit(
        name = "Legs",
        emoji = "🦵",
        dayLabel = "Day 3 & 6",
        musclesFocus = "Quads • Hamstrings • Glutes • Calves",
        estimatedMinutes = 55,
        difficulty = "Intermediate",
        exercises = listOf(
            Exercise(
                name = "Bodyweight Squat",
                muscleGroup = "Quads & Glutes",
                sets = 4, reps = "15-20", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/l4FGDsnGWuHYKHMBy/giphy.gif",
                instructions = listOf(
                    "Stand feet shoulder-width apart",
                    "Push hips back and bend knees",
                    "Go until thighs are parallel to floor",
                    "Drive through heels to stand up"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 8
            ),
            Exercise(
                name = "Dumbbell Goblet Squat",
                muscleGroup = "Quads & Glutes",
                sets = 4, reps = "12-15", restSeconds = 75,
                gifRes = "https://media.giphy.com/media/xT9IgzoKnwFNmISR8I/giphy.gif",
                instructions = listOf(
                    "Hold dumbbell vertically at chest",
                    "Feet slightly wider than shoulder-width",
                    "Squat deep keeping chest up",
                    "Elbows push knees out at bottom"
                ),
                difficulty = "Beginner", equipment = "Dumbbell", calories = 10
            ),
            Exercise(
                name = "Romanian Deadlift",
                muscleGroup = "Hamstrings & Glutes",
                sets = 4, reps = "10-12", restSeconds = 90,
                gifRes = "https://media.giphy.com/media/3o7TKSx0g7RqRnVqQg/giphy.gif",
                instructions = listOf(
                    "Hold dumbbells in front of thighs",
                    "Hinge at hips, push them back",
                    "Lower weights along legs",
                    "Feel hamstring stretch, drive hips forward to stand"
                ),
                difficulty = "Intermediate", equipment = "Dumbbell", calories = 11
            ),
            Exercise(
                name = "Walking Lunges",
                muscleGroup = "Quads & Glutes",
                sets = 3, reps = "12 each leg", restSeconds = 75,
                gifRes = "https://media.giphy.com/media/3o7TKMt4aBz7G3wVWM/giphy.gif",
                instructions = listOf(
                    "Step forward with one leg",
                    "Lower back knee toward floor",
                    "Front knee stays over ankle",
                    "Step forward with back leg — repeat"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 9
            ),
            Exercise(
                name = "Calf Raises",
                muscleGroup = "Calves",
                sets = 4, reps = "20-25", restSeconds = 45,
                gifRes = "https://media.giphy.com/media/3o7TKRwpns23QMNNiU/giphy.gif",
                instructions = listOf(
                    "Stand on edge of step or flat ground",
                    "Rise up on toes as high as possible",
                    "Hold 1 second at top",
                    "Lower slowly — full stretch at bottom"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 5
            ),
            Exercise(
                name = "Glute Bridge",
                muscleGroup = "Glutes & Hamstrings",
                sets = 3, reps = "15-20", restSeconds = 60,
                gifRes = "https://media.giphy.com/media/3o7TKF1fSIs1R19B8k/giphy.gif",
                instructions = listOf(
                    "Lie on back, knees bent, feet flat",
                    "Push hips up squeezing glutes",
                    "Hold 2 seconds at top",
                    "Lower slowly and repeat"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 6
            )
        )
    ),

    WorkoutSplit(
        name = "Rest & Recovery",
        emoji = "🧘",
        dayLabel = "Day 7",
        musclesFocus = "Active Recovery",
        estimatedMinutes = 20,
        difficulty = "Beginner",
        exercises = listOf(
            Exercise(
                name = "Full Body Stretch",
                muscleGroup = "Full Body",
                sets = 1, reps = "Hold 30 sec each", restSeconds = 0,
                gifRes = "https://media.giphy.com/media/l4FGrYKtP0pBGpBAA/giphy.gif",
                instructions = listOf(
                    "Neck rolls — 10 each direction",
                    "Shoulder cross stretch — 30 sec each",
                    "Hip flexor stretch — 30 sec each side",
                    "Hamstring stretch — 30 sec each side",
                    "Child's pose — 60 seconds"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 2
            ),
            Exercise(
                name = "Light Walk",
                muscleGroup = "Cardio",
                sets = 1, reps = "20 minutes", restSeconds = 0,
                gifRes = "https://media.giphy.com/media/3o7TKSjRrfIPjeiVyM/giphy.gif",
                instructions = listOf(
                    "Walk at comfortable pace",
                    "Helps flush lactic acid",
                    "Good for blood circulation",
                    "Promotes hair growth through blood flow"
                ),
                difficulty = "Beginner", equipment = "Bodyweight", calories = 80
            )
        )
    )
)

// ── Diet plan calculation ─────────────────────────────────────────────────────
data class DietPlan(
    val targetCalories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int,
    val meals: List<DietMeal>,
    val weeklyPlan: Map<String, List<DietMeal>>,
    val supplements: List<String>,
    val tips: List<String>
)

data class DietMeal(
    val mealName: String,
    val time: String,
    val foods: List<String>,
    val calories: Int,
    val protein: Int
)

fun generateVegEggDietPlan(
    weightKg: Float,
    targetWeightKg: Float,
    heightCm: Float,
    age: Int
): DietPlan {
    val bmi = weightKg / ((heightCm / 100f) * (heightCm / 100f))
    val bmr = 10 * weightKg + 6.25f * heightCm - 5 * age + 5
    val tdee = bmr * 1.55f // Moderate activity with workout
    val targetCal = if (weightKg > targetWeightKg) (tdee - 300).toInt() // cut
                    else if (weightKg < targetWeightKg) (tdee + 200).toInt() // bulk
                    else tdee.toInt() // maintain

    val protein = (targetWeightKg * 2.2f).toInt()
    val fat     = (targetCal * 0.25f / 9).toInt()
    val carbs   = ((targetCal - protein * 4 - fat * 9) / 4).coerceAtLeast(0).toInt()
    val fiber   = 35

    val meals = listOf(
        DietMeal(
            mealName = "Pre-Workout / Breakfast",
            time     = "7:00 - 8:00 AM",
            foods    = listOf(
                "3 Whole eggs (scrambled/boiled)",
                "2 Egg whites",
                "2 Chapati / 1 cup Oats",
                "1 Banana",
                "1 glass Milk with protein powder (if available)"
            ),
            calories = (targetCal * 0.28f).toInt(),
            protein  = (protein * 0.30f).toInt()
        ),
        DietMeal(
            mealName = "Mid Morning Snack",
            time     = "10:30 - 11:00 AM",
            foods    = listOf(
                "1 handful Almonds (10-12)",
                "1 handful Walnuts (5-6)",
                "1 Apple or Guava",
                "Green tea (no sugar)"
            ),
            calories = (targetCal * 0.10f).toInt(),
            protein  = (protein * 0.08f).toInt()
        ),
        DietMeal(
            mealName = "Lunch",
            time     = "1:00 - 1:30 PM",
            foods    = listOf(
                "1.5 cup Rice or 3 Chapati",
                "1 cup Dal (Toor/Moong/Chana)",
                "1 cup Mixed vegetables sabzi",
                "1 cup Curd/Raita",
                "Salad — cucumber, tomato, onion",
                "1 tsp ghee on dal"
            ),
            calories = (targetCal * 0.30f).toInt(),
            protein  = (protein * 0.28f).toInt()
        ),
        DietMeal(
            mealName = "Evening Snack",
            time     = "4:00 - 4:30 PM",
            foods    = listOf(
                "1 cup Sprouts (moong/chana)",
                "1 Banana or 1 cup Papaya",
                "Buttermilk / Chaas",
                "Handful of Peanuts"
            ),
            calories = (targetCal * 0.12f).toInt(),
            protein  = (protein * 0.12f).toInt()
        ),
        DietMeal(
            mealName = "Post-Workout / Dinner",
            time     = "7:30 - 8:00 PM",
            foods    = listOf(
                "2 Egg whites + 1 whole egg",
                "1 cup Paneer (100g) or 1 cup Rajma",
                "2 Chapati",
                "1 cup Sabzi (avoid heavy oil)",
                "Small salad"
            ),
            calories = (targetCal * 0.25f).toInt(),
            protein  = (protein * 0.28f).toInt()
        ),
        DietMeal(
            mealName = "Before Bed",
            time     = "9:30 - 10:00 PM",
            foods    = listOf(
                "1 glass warm milk",
                "4-5 Almonds soaked",
                "Optional: 1 tsp turmeric in milk (haldi doodh)"
            ),
            calories = (targetCal * 0.08f).toInt(),
            protein  = (protein * 0.06f).toInt()
        )
    )

    val weeklyPlan = mapOf(
        "Monday (Push + High Protein)" to listOf(meals[0], meals[1], meals[2], meals[3], meals[4], meals[5]),
        "Tuesday (Pull + Recovery)" to listOf(
            meals[0].copy(foods = listOf("4 egg whites + 1 whole egg", "2 Idli or Dosa", "1 Banana")),
            meals[1], meals[2], meals[3],
            meals[4].copy(foods = listOf("1 cup Rajma", "2 Chapati", "Curd", "Salad")),
            meals[5]
        ),
        "Wednesday (Legs + Carb Focus)" to listOf(
            meals[0].copy(foods = listOf("3 eggs", "2 cups Oats with milk", "1 Banana", "Nuts")),
            meals[1], meals[2], meals[3], meals[4], meals[5]
        ),
        "Thursday (Push + Same as Monday)" to listOf(meals[0], meals[1], meals[2], meals[3], meals[4], meals[5]),
        "Friday (Pull + High Protein)" to listOf(
            meals[0].copy(foods = listOf("4 eggs scrambled", "Brown rice 1 cup", "1 glass milk")),
            meals[1], meals[2], meals[3],
            meals[4].copy(foods = listOf("100g Paneer", "Dal", "2 Chapati", "Salad")),
            meals[5]
        ),
        "Saturday (Legs + Carb Load)" to listOf(
            meals[0].copy(foods = listOf("3 eggs", "3 Chapati", "Sabzi", "Milk")),
            meals[1],
            meals[2].copy(foods = listOf("2 cups Rice", "Dal", "Sabzi", "Curd", "Salad")),
            meals[3], meals[4], meals[5]
        ),
        "Sunday (Rest + Light Eating)" to listOf(
            meals[0].copy(foods = listOf("2 eggs", "Poha or Upma", "Fruits")),
            meals[1],
            meals[2].copy(foods = listOf("1 cup Rice", "Dal", "Sabzi", "Curd")),
            meals[3].copy(foods = listOf("Fruit bowl", "Green tea")),
            meals[4].copy(foods = listOf("1 cup Dal", "2 Chapati", "Sabzi")),
            meals[5]
        )
    )

    val supplements = listOf(
        "✅ Whey Protein — 1 scoop after workout (optional but helpful)",
        "✅ Creatine 5g/day — improves strength significantly",
        "✅ T.Trip-D (Vitamin D3) — you already take this ✓",
        "✅ T.Rucal CM (Calcium) — you already take this ✓",
        "✅ T.Biotree (Biotin) — you already take this ✓",
        "⚠️ Vitamin B12 — your level was low (270.9 pg/mL), consider supplement",
        "✅ Omega-3 (Fish oil / Flaxseed oil) — reduces inflammation"
    )

    val tips = listOf(
        "🥚 Eat eggs daily — best complete protein for muscle growth",
        "⏰ Eat every 3-4 hours — keeps protein synthesis active",
        "💧 Drink 3L water daily — you're already tracking this",
        "🌙 Casein protein before bed — milk/paneer is perfect",
        "🍌 Banana before workout — instant energy",
        "🥜 Soaked almonds morning — better absorption",
        "❌ Avoid maida, fried foods, sugar, alcohol",
        "🫘 Dal twice daily — cheapest quality protein source",
        "🧀 100g Paneer = 18g protein — have it daily",
        "📅 Cheat meal Sunday only — one meal not one day"
    )

    return DietPlan(
        targetCalories = targetCal,
        protein = protein,
        carbs = carbs,
        fat = fat,
        fiber = fiber,
        meals = meals,
        weeklyPlan = weeklyPlan,
        supplements = supplements,
        tips = tips
    )
}

// ── Weekly split schedule ─────────────────────────────────────────────────────
fun getWeeklySchedule() = mapOf(
    "Monday"    to "Push 💪",
    "Tuesday"   to "Pull 🏋️",
    "Wednesday" to "Legs 🦵",
    "Thursday"  to "Push 💪",
    "Friday"    to "Pull 🏋️",
    "Saturday"  to "Legs 🦵",
    "Sunday"    to "Rest 🧘"
)

fun getTodaySplit(): WorkoutSplit {
    val day = java.time.LocalDate.now().dayOfWeek
    return when (day) {
        java.time.DayOfWeek.MONDAY, java.time.DayOfWeek.THURSDAY -> PPL_SPLIT[0]
        java.time.DayOfWeek.TUESDAY, java.time.DayOfWeek.FRIDAY  -> PPL_SPLIT[1]
        java.time.DayOfWeek.WEDNESDAY, java.time.DayOfWeek.SATURDAY -> PPL_SPLIT[2]
        else -> PPL_SPLIT[3]
    }
}