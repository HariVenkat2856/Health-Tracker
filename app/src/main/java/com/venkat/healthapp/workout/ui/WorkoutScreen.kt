package com.venkat.healthapp.workout.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.workout.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(vm: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Today", "Split", "Diet Plan", "Progress")

    // First time setup check
    val profile by vm.userProfile.collectAsState()
    val showSetup = profile == null || profile?.weightKg == 0f || profile?.heightCm == 0f

    if (showSetup) {
        WorkoutSetupScreen(vm)
        return
    }

    Column(Modifier.fillMaxSize().background(BgDark)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor   = CardDark,
            contentColor     = Accent,
            edgePadding      = 16.dp
        ) {
            tabs.forEachIndexed { i, t ->
                Tab(
                    selected = selectedTab == i,
                    onClick  = { selectedTab = i },
                    text     = {
                        Text(t, fontSize = 13.sp,
                            fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == i) Accent else TextMuted)
                    }
                )
            }
        }
        when (selectedTab) {
            0 -> TodayWorkoutTab(vm)
            1 -> SplitOverviewTab(vm)
            2 -> DietPlanTab(vm)
            3 -> WorkoutProgressTab(vm)
        }
    }
}

// ── First time setup ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSetupScreen(vm: MainViewModel) {
    var weight   by remember { mutableStateOf("") }
    var height   by remember { mutableStateOf("") }
    var age      by remember { mutableStateOf("26") }
    var targetW  by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text("🏋️", fontSize = 60.sp)
        Text("Workout Setup",
            fontSize = 28.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text("Enter your details to get a personalized\nworkout and diet plan",
            fontSize = 14.sp, color = TextMuted, textAlign = TextAlign.Center)

        Spacer(Modifier.height(8.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
            border = BorderStroke(1.dp, Accent.copy(0.4f))
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Your Body Stats",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Accent)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SetupTextField("Weight (kg)", weight, Modifier.weight(1f)) { weight = it }
                    SetupTextField("Height (cm)", height, Modifier.weight(1f)) { height = it }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SetupTextField("Age", age, Modifier.weight(1f)) { age = it }
                    SetupTextField("Target Weight (kg)", targetW, Modifier.weight(1f)) { targetW = it }
                }

                // BMI preview
                val w = weight.toFloatOrNull()
                val h = height.toFloatOrNull()
                if (w != null && h != null && w > 0 && h > 0) {
                    val bmi = w / ((h / 100f) * (h / 100f))
                    val idealWeight = 22f * ((h / 100f) * (h / 100f))
                    Box(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentAlpha)
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("📊 Your Stats Preview", fontSize = 13.sp,
                                color = Accent, fontWeight = FontWeight.SemiBold)
                            Text("BMI: %.1f — %s".format(bmi,
                                when { bmi < 18.5f -> "Underweight" ; bmi < 25f -> "Normal ✅"; bmi < 30f -> "Overweight"; else -> "Obese" }),
                                fontSize = 13.sp, color = TextPrimary)
                            Text("Ideal weight for aesthetic body (BMI 22): %.1f kg".format(idealWeight),
                                fontSize = 13.sp, color = TextPrimary)
                            Text("Goal: ${if (w > idealWeight) "Fat Loss + Muscle Gain" else if (w < idealWeight) "Lean Bulk" else "Body Recomposition"}",
                                fontSize = 13.sp, color = Gold, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Button(
                    onClick = {
                        if (weight.isNotBlank() && height.isNotBlank()) {
                            vm.saveProfile(
                                com.venkat.healthapp.food.data.UserProfile(
                                    weightKg      = weight.toFloatOrNull() ?: 0f,
                                    heightCm      = height.toFloatOrNull() ?: 0f,
                                    age           = age.toIntOrNull() ?: 26,
                                    goal          = "Aesthetic",
                                    activityLevel = "Moderate",
                                    targetWeightKg = targetW.toFloatOrNull()
                                        ?: (22f * ((height.toFloatOrNull()?.div(100f) ?: 1.7f).let { it * it }))
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent),
                    enabled  = weight.isNotBlank() && height.isNotBlank()
                ) {
                    Text("Generate My Plan 🚀",
                        color = Color.Black, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupTextField(label: String, value: String, modifier: Modifier, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label, fontSize = 11.sp, color = TextMuted) },
        modifier = modifier, shape = RoundedCornerShape(10.dp), singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent, unfocusedBorderColor = BorderDark,
            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = Accent
        )
    )
}

// ── TODAY workout tab ─────────────────────────────────────────────────────────
@Composable
fun TodayWorkoutTab(vm: MainViewModel) {
    val todaySplit   = remember { getTodaySplit() }
    val todayLogs    by vm.workoutLogs.collectAsState()
    val completedIds = todayLogs.filter { it.completed }.map { it.exerciseName }.toSet()
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Today's split header
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(
                        when (todaySplit.name) {
                            "Push" -> listOf(Color(0xFF1A0D2E), Color(0xFF0D1117))
                            "Pull" -> listOf(Color(0xFF0D2A1F), Color(0xFF0D1117))
                            "Legs" -> listOf(Color(0xFF2A1A0D), Color(0xFF0D1117))
                            else   -> listOf(Color(0xFF0A1628), Color(0xFF0D1117))
                        }
                    ))
                    .border(1.dp,
                        when (todaySplit.name) {
                            "Push" -> Purple.copy(0.4f); "Pull" -> Accent.copy(0.4f)
                            "Legs" -> Gold.copy(0.4f);   else   -> AccentBlue.copy(0.4f)
                        },
                        RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(todaySplit.emoji + "  " + todaySplit.name + " Day",
                        fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text(todaySplit.musclesFocus, color = TextMuted, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("⏱ ${todaySplit.estimatedMinutes} min",
                            when(todaySplit.name){"Push"->Purple;"Pull"->Accent;"Legs"->Gold;else->AccentBlue})
                        InfoChip("${completedIds.size}/${todaySplit.exercises.size} done", Accent)
                        InfoChip(todaySplit.difficulty, Gold)
                    }
                    // Mini progress bar
                    Spacer(Modifier.height(10.dp))
                    val prog = if (todaySplit.exercises.isNotEmpty())
                        completedIds.size.toFloat() / todaySplit.exercises.size else 0f
                    Box(Modifier.fillMaxWidth().height(6.dp)
                        .clip(RoundedCornerShape(100.dp)).background(BorderDark)) {
                        Box(Modifier.fillMaxWidth(prog).height(6.dp)
                            .background(Accent, RoundedCornerShape(100.dp)))
                    }
                }
            }
        }

        // Exercise cards
        items(todaySplit.exercises) { exercise ->
            val isDone = exercise.name in completedIds
            ExerciseCard(
                exercise  = exercise,
                isDone    = isDone,
                onToggle  = { vm.toggleExercise(exercise.name, isDone) },
                onTap     = { selectedExercise = exercise }
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }

    // Exercise detail sheet
    selectedExercise?.let { ex ->
        ExerciseDetailSheet(exercise = ex, onDismiss = { selectedExercise = null })
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise, isDone: Boolean,
    onToggle: () -> Unit, onTap: () -> Unit
) {
    val color = when (exercise.muscleGroup) {
        "Chest", "Upper Chest" -> Purple
        "Shoulders"            -> AccentBlue
        "Triceps"              -> RedPill
        "Back"                 -> Accent
        "Biceps", "Biceps & Forearms" -> Gold
        "Rear Delts"           -> Color(0xFF4CAF50)
        "Quads & Glutes", "Quads & Glutes" -> Gold
        "Hamstrings & Glutes"  -> Color(0xFFFF9800)
        "Calves"               -> AccentBlue
        else                   -> TextMuted
    }

    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDone) AccentAlpha else CardDark)
            .border(1.dp, if (isDone) Accent.copy(0.5f) else BorderDark, RoundedCornerShape(16.dp))
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Done toggle
            Box(
                Modifier.size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDone) Accent else BorderDark)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isDone) Icon(Icons.Default.Check, null,
                    tint = Color.Black, modifier = Modifier.size(18.dp))
            }

            Column(Modifier.weight(1f)) {
                Text(exercise.name,
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("${exercise.sets} sets × ${exercise.reps} reps  •  ${exercise.restSeconds}s rest",
                    fontSize = 12.sp, color = TextMuted)
                Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoChip(exercise.muscleGroup, color)
                    InfoChip(exercise.equipment, BorderDark.copy(alpha = 1f))
                }
            }

            // GIF preview + detail button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(exercise.gifRes).crossfade(true).build(),
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                        .background(Card2Dark).clickable { onTap() }
                )
                Text("How to", fontSize = 9.sp, color = TextMuted)
            }
        }
    }
}

// ── Exercise detail bottom sheet ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailSheet(exercise: Exercise, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        contentColor     = TextPrimary
    ) {
        Column(
            Modifier.padding(20.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(exercise.name,
                fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(exercise.muscleGroup, color = Accent, fontSize = 13.sp)

            // GIF
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(exercise.gifRes).crossfade(true).build(),
                contentDescription = exercise.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(200.dp)
                    .clip(RoundedCornerShape(16.dp)).background(Card2Dark)
            )

            // Stats row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip("Sets", "${exercise.sets}", Purple, Modifier.weight(1f))
                StatChip("Reps", exercise.reps, Accent, Modifier.weight(1f))
                StatChip("Rest", "${exercise.restSeconds}s", Gold, Modifier.weight(1f))
                StatChip("~Cal", "${exercise.calories}/set", RedPill, Modifier.weight(1f))
            }

            Text("How to perform:",
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            exercise.instructions.forEachIndexed { i, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        Modifier.size(22.dp).clip(CircleShape).background(AccentAlpha),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${i + 1}", fontSize = 11.sp, color = Accent,
                            fontWeight = FontWeight.Bold)
                    }
                    Text(step, fontSize = 13.sp, color = TextPrimary)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(color.copy(0.1f))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(10.dp)).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
fun InfoChip(text: String, color: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(100.dp))
            .background(color.copy(0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

// ── Split overview tab ────────────────────────────────────────────────────────
@Composable
fun SplitOverviewTab(vm: MainViewModel) {
    var expandedSplit by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("PPL Split Schedule", fontSize = 20.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
            Text("Push Pull Legs — best split for aesthetic body",
                color = TextMuted, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
        }

        // Weekly schedule
        item {
            Card(
                Modifier.fillMaxWidth(),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Weekly Schedule", fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    getWeeklySchedule().forEach { (day, split) ->
                        val isToday = day.equals(
                            LocalDate.now().dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() },
                            ignoreCase = true
                        )
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isToday) AccentAlpha else Color.Transparent)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(day, fontSize = 13.sp,
                                color = if (isToday) Accent else TextPrimary,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(split, fontSize = 13.sp,
                                    color = if (isToday) Accent else TextMuted)
                                if (isToday) Text("← Today",
                                    fontSize = 11.sp, color = Accent)
                            }
                        }
                    }
                }
            }
        }

        // Split detail cards
        items(PPL_SPLIT) { split ->
            val isExpanded = expandedSplit == split.name
            val splitColor = when(split.name) {
                "Push" -> Purple; "Pull" -> Accent
                "Legs" -> Gold;   else   -> AccentBlue
            }
            Column(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .border(1.dp, splitColor.copy(0.4f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    Modifier.fillMaxWidth().clickable {
                        expandedSplit = if (isExpanded) null else split.name
                    }.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(split.emoji, fontSize = 28.sp)
                        Column {
                            Text(split.name + " Day",
                                fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(split.musclesFocus, fontSize = 12.sp, color = TextMuted)
                            Text(split.dayLabel, fontSize = 11.sp, color = splitColor)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${split.exercises.size} exercises",
                            fontSize = 12.sp, color = TextMuted)
                        Text("~${split.estimatedMinutes} min",
                            fontSize = 12.sp, color = splitColor)
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null, tint = TextMuted
                        )
                    }
                }
                if (isExpanded) {
                    HorizontalDivider(color = BorderDark)
                    split.exercises.forEach { ex ->
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp, 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(ex.gifRes).crossfade(true).build(),
                                contentDescription = ex.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)).background(Card2Dark)
                            )
                            Column(Modifier.weight(1f)) {
                                Text(ex.name, fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium, color = TextPrimary)
                                Text("${ex.sets}×${ex.reps}  •  ${ex.muscleGroup}",
                                    fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Diet plan tab ─────────────────────────────────────────────────────────────
@Composable
fun DietPlanTab(vm: MainViewModel) {
    val profile by vm.userProfile.collectAsState()
    val dietPlan = remember(profile) {
        profile?.let {
            generateVegEggDietPlan(it.weightKg, it.targetWeightKg, it.heightCm, it.age)
        }
    }
    var selectedDay by remember { mutableStateOf("Monday (Push + High Protein)") }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Your Diet Plan", fontSize = 20.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
            Text("Veg + Egg • Aesthetic body goal",
                color = TextMuted, fontSize = 13.sp)
        }

        dietPlan?.let { plan ->
            // Macro targets
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                    border = BorderStroke(1.dp, Gold.copy(0.4f))
                ) {
                    Column(Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Daily Targets",
                            fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Gold)
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MacroTarget("🔥","${plan.targetCalories}","kcal", Gold, Modifier.weight(1f))
                            MacroTarget("💪","${plan.protein}g","protein", Accent, Modifier.weight(1f))
                            MacroTarget("🌾","${plan.carbs}g","carbs", AccentBlue, Modifier.weight(1f))
                            MacroTarget("🥑","${plan.fat}g","fat", Purple, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Day selector
            item {
                Text("Weekly Plan", fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = TextPrimary)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(plan.weeklyPlan.keys.toList()) { day ->
                        val sel = selectedDay == day
                        val shortDay = day.split(" ").first()
                        Box(
                            Modifier.clip(RoundedCornerShape(10.dp))
                                .background(if (sel) Accent else Card2Dark)
                                .border(1.dp, if (sel) Accent else BorderDark, RoundedCornerShape(10.dp))
                                .clickable { selectedDay = day }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(shortDay, fontSize = 12.sp,
                                color = if (sel) Color.Black else TextMuted,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            // Meals for selected day
            val dayMeals = plan.weeklyPlan[selectedDay] ?: plan.meals
            items(dayMeals) { meal ->
                DietMealCard(meal)
            }

            // Supplements
            item {
                Text("Supplements", fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Card(
                    Modifier.fillMaxWidth(),
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                    border = BorderStroke(1.dp, Purple.copy(0.3f))
                ) {
                    Column(Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.supplements.forEach { s ->
                            Text(s, fontSize = 13.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // Tips
            item {
                Text("Diet Tips", fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Card(
                    Modifier.fillMaxWidth(),
                    shape  = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                    border = BorderStroke(1.dp, Accent.copy(0.3f))
                ) {
                    Column(Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plan.tips.forEach { tip ->
                            Text(tip, fontSize = 13.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun MacroTarget(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(color.copy(0.1f))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(10.dp)).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 14.sp)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 9.sp, color = TextMuted)
    }
}

@Composable
fun DietMealCard(meal: DietMeal) {
    Card(
        Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(meal.mealName, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(meal.time, fontSize = 11.sp, color = Accent)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${meal.calories} kcal",
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Gold)
                    Text("P: ${meal.protein}g",
                        fontSize = 11.sp, color = Accent)
                }
            }
            meal.foods.forEach { food ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top) {
                    Text("•", fontSize = 13.sp, color = Accent)
                    Text(food, fontSize = 13.sp, color = TextPrimary)
                }
            }
        }
    }
}

// ── Progress tab ──────────────────────────────────────────────────────────────
@Composable
fun WorkoutProgressTab(vm: MainViewModel) {
    val totalDays  by vm.workoutTotalDays.collectAsState()
    val allDates   by vm.workoutDates.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Workout Progress", fontSize = 20.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SleepStatCard("💪", "$totalDays", "Total\nWorkouts", Accent, Modifier.weight(1f))
                SleepStatCard("🔥", "${allDates.size}", "Days\nActive", Gold, Modifier.weight(1f))
                SleepStatCard("📅", if (allDates.isNotEmpty()) "${allDates.size}/7\nthis week" else "0", "Weekly", Purple, Modifier.weight(1f))
            }
        }

        // Workout calendar
        item {
            Text("History", fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }

        if (allDates.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏋️", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No workouts logged yet", color = TextMuted)
                        Text("Complete exercises in Today tab", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(allDates.take(30)) { date ->
                val display = runCatching {
                    LocalDate.parse(date).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
                }.getOrDefault(date)
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp)).background(CardDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(14.dp, 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("💪", fontSize = 16.sp)
                        Text(display, fontSize = 13.sp, color = TextPrimary)
                    }
                    Text("Workout ✅", fontSize = 12.sp, color = Accent)
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// Import StatCard from sleep screen
@Composable
fun SleepStatCard(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(14.dp)).background(CardDark)
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(14.dp)).padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(emoji, fontSize = 20.sp)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
        }
    }
}