package com.venkat.healthapp.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.food.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(vm: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Today", "Search", "My Foods", "Goals")

    Column(Modifier.fillMaxSize().background(BgDark)) {
        // Tab row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardDark,
            contentColor = Accent,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { i, t ->
                Tab(selected = selectedTab == i, onClick = { selectedTab = i },
                    text = { Text(t, fontSize = 13.sp, fontWeight = if (selectedTab==i) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab==i) Accent else TextMuted) })
            }
        }
        when (selectedTab) {
            0 -> TodayFoodTab(vm)
            1 -> SearchFoodTab(vm)
            2 -> MyFoodsTab(vm)
            3 -> GoalsTab(vm)
        }
    }
}

// ── TODAY tab ─────────────────────────────────────────────────────────────────
@Composable
fun TodayFoodTab(vm: MainViewModel) {
    val logs     by vm.todayFoodLogs.collectAsState()
    val nutrition by vm.todayNutrition.collectAsState()
    val targets  by vm.nutritionTargets.collectAsState()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Today's Nutrition", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
        }
        // Macro cards
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MacroCard("🔥","Calories","${(nutrition["calories"]?:0f).toInt()}","${targets?.calories?:0} kcal",Gold,Modifier.weight(1f))
                MacroCard("💪","Protein","${(nutrition["protein"]?:0f).toInt()}g","${targets?.protein?:0}g",Accent,Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MacroCard("🌾","Carbs","${(nutrition["carbs"]?:0f).toInt()}g","${targets?.carbs?:0}g",AccentBlue,Modifier.weight(1f))
                MacroCard("🥑","Fat","${(nutrition["fat"]?:0f).toInt()}g","${targets?.fat?:0}g",Purple,Modifier.weight(1f))
                MacroCard("🌿","Fiber","${(nutrition["fiber"]?:0f).toInt()}g","${targets?.fiber?:0}g",Color(0xFF4CAF50),Modifier.weight(1f))
            }
        }
        // Meal groups
        listOf("Breakfast","Lunch","Dinner","Snack").forEach { meal ->
            val mealLogs = logs.filter { it.mealType == meal }
            item {
                MealSection(meal, mealLogs, onDelete = { vm.deleteFoodLog(it) })
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun MacroCard(emoji: String, label: String, value: String, target: String, color: Color, modifier: Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(14.dp)).background(CardDark)
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(14.dp)).padding(12.dp)
    ) {
        Column {
            Text(emoji, fontSize = 16.sp)
            Spacer(Modifier.height(3.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = TextMuted)
            Text(target, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
fun MealSection(meal: String, logs: List<FoodLog>, onDelete: (FoodLog) -> Unit) {
    val mealEmoji = mapOf("Breakfast" to "🌅", "Lunch" to "☀️", "Dinner" to "🌙", "Snack" to "🍎")
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(mealEmoji[meal] ?: "🍽", fontSize = 16.sp)
            Spacer(Modifier.width(6.dp))
            Text(meal, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            if (logs.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Text("${logs.sumOf { it.calories.toDouble() }.toInt()} kcal",
                    fontSize = 12.sp, color = Gold)
            }
        }
        Spacer(Modifier.height(6.dp))
        if (logs.isEmpty()) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Card2Dark)
                .padding(14.dp), contentAlignment = Alignment.Center) {
                Text("No $meal logged yet", color = TextMuted, fontSize = 12.sp)
            }
        } else {
            logs.forEach { log ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp)).background(CardDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(log.foodName, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text("${log.quantity}x ${log.unit}  •  P:${log.protein.toInt()}g  C:${log.carbs.toInt()}g  F:${log.fat.toInt()}g",
                            fontSize = 11.sp, color = TextMuted)
                    }
                    Text("${log.calories.toInt()}", fontSize = 14.sp, color = Gold, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { onDelete(log) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, tint = RedPill, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ── SEARCH tab ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFoodTab(vm: MainViewModel) {
    var query        by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Breakfast") }
    var selectedItem by remember { mutableStateOf<FoodItem?>(null) }
    var qty          by remember { mutableStateOf("1") }
    val results      by vm.searchResults.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Search & Add Food", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; vm.search(it) },
            placeholder = { Text("Search Indian foods... e.g. Rice, Banana", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Accent) },
            trailingIcon = if (query.isNotEmpty()) {{ IconButton({ query=""; vm.search("") }) {
                Icon(Icons.Default.Close, null, tint = TextMuted) } }} else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent, unfocusedBorderColor = BorderDark,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                cursorColor = Accent, focusedContainerColor = CardDark, unfocusedContainerColor = CardDark
            )
        )

        Spacer(Modifier.height(10.dp))

        // Meal selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Breakfast","Lunch","Dinner","Snack").forEach { meal ->
                val sel = selectedMeal == meal
                Box(
                    Modifier.clip(RoundedCornerShape(100.dp))
                        .background(if (sel) Accent else Card2Dark)
                        .border(1.dp, if (sel) Accent else BorderDark, RoundedCornerShape(100.dp))
                        .clickable { selectedMeal = meal }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(meal, fontSize = 12.sp, color = if (sel) Color.Black else TextMuted,
                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Auto-suggest results
        if (results.isNotEmpty()) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(results) { item ->
                    FoodSearchItem(item = item, isSelected = selectedItem == item,
                        onClick = { selectedItem = if (selectedItem == item) null else item })
                }
            }
        } else if (query.isNotEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No foods found. Add a custom food below!", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            // Category chips
            CategoryBrowser(vm) { item -> selectedItem = item }
        }

        // Add food panel
        selectedItem?.let { item ->
            Spacer(Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Card2Dark, contentColor = TextPrimary),
                border = BorderStroke(1.dp, Accent.copy(0.4f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Per ${item.unit}: ${item.calories.toInt()} kcal | P:${item.protein}g C:${item.carbs}g F:${item.fat}g",
                        fontSize = 12.sp, color = TextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = qty, onValueChange = { qty = it },
                            label = { Text("Quantity", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.width(100.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Accent, unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = Accent
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        val q = qty.toFloatOrNull() ?: 1f
                        Text("= ${(item.calories * q).toInt()} kcal", color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            vm.logFood(item, qty.toFloatOrNull() ?: 1f, selectedMeal)
                            selectedItem = null; query = ""; vm.search("")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add to $selectedMeal", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FoodSearchItem(item: FoodItem, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AccentAlpha else CardDark)
            .border(1.dp, if (isSelected) Accent else BorderDark, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp, 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.name, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                Text("${item.category} • ${item.unit}", fontSize = 11.sp, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${item.calories.toInt()} kcal", fontSize = 13.sp, color = Gold, fontWeight = FontWeight.Bold)
                Text("P:${item.protein}g", fontSize = 11.sp, color = Accent)
            }
        }
    }
}

@Composable
fun CategoryBrowser(vm: MainViewModel, onSelect: (FoodItem) -> Unit) {
    val allItems by vm.allFoodItems().collectAsState()
    val categories = listOf("Fruits","Nuts","Grains","Vegetables","Protein","Dairy","Snacks")
    var expanded by remember { mutableStateOf<String?>(null) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(categories) { cat ->
            val items = allItems.filter { it.category == cat }
            val isExp = expanded == cat
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(CardDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            ) {
                Row(
                    Modifier.fillMaxWidth().clickable { expanded = if (isExp) null else cat }.padding(14.dp, 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(cat, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${items.size} foods", fontSize = 12.sp, color = TextMuted)
                        Icon(if (isExp) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            null, tint = TextMuted, modifier = Modifier.size(20.dp))
                    }
                }
                if (isExp) {
                    items.forEach { item ->
                        Box(Modifier.fillMaxWidth().clickable { onSelect(item) }
                            .padding(start = 14.dp, end = 14.dp, bottom = 8.dp)) {
                            FoodSearchItem(item = item, isSelected = false) { onSelect(item) }
                        }
                    }
                }
            }
        }
    }
}

// ── MY FOODS tab ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFoodsTab(vm: MainViewModel) {
    var showAdd   by remember { mutableStateOf(false) }
    var heading   by remember { mutableStateOf("") }
    var about     by remember { mutableStateOf("") }
    var calories  by remember { mutableStateOf("") }
    var protein   by remember { mutableStateOf("") }
    var carbs     by remember { mutableStateOf("") }
    var fat       by remember { mutableStateOf("") }
    var fiber     by remember { mutableStateOf("") }
    var unit      by remember { mutableStateOf("1 serving") }
    val allItems  by vm.allFoodItems().collectAsState()
    val customItems = allItems.filter { it.isCustom }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("My Custom Foods", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            FloatingActionButton(onClick = { showAdd = !showAdd }, containerColor = Accent,
                contentColor = Color.Black, modifier = Modifier.size(42.dp)) {
                Icon(if (showAdd) Icons.Default.Close else Icons.Default.Add, null)
            }
        }
        Spacer(Modifier.height(12.dp))

        if (showAdd) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Card2Dark, contentColor = TextPrimary),
                border = BorderStroke(1.dp, Accent.copy(0.4f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Custom Food", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    FoodTextField("Food Name / Heading *", heading) { heading = it }
                    FoodTextField("About this food (description)", about) { about = it }
                    FoodTextField("Serving unit (e.g. 1 bowl, 100g)", unit) { unit = it }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FoodTextField("Calories",calories,Modifier.weight(1f)) { calories=it }
                        FoodTextField("Protein(g)",protein,Modifier.weight(1f)) { protein=it }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FoodTextField("Carbs(g)",carbs,Modifier.weight(1f)) { carbs=it }
                        FoodTextField("Fat(g)",fat,Modifier.weight(1f)) { fat=it }
                        FoodTextField("Fiber(g)",fiber,Modifier.weight(1f)) { fiber=it }
                    }
                    Button(
                        onClick = {
                            if (heading.isNotBlank()) {
                                vm.addCustomFood(FoodItem(
                                    name = heading, category = "Custom",
                                    unit = unit.ifBlank { "1 serving" },
                                    calories = calories.toFloatOrNull() ?: 0f,
                                    protein = protein.toFloatOrNull() ?: 0f,
                                    carbs = carbs.toFloatOrNull() ?: 0f,
                                    fat = fat.toFloatOrNull() ?: 0f,
                                    fiber = fiber.toFloatOrNull() ?: 0f,
                                    isCustom = true
                                ))
                                heading=""; about=""; calories=""; protein=""; carbs=""; fat=""; fiber=""; unit="1 serving"
                                showAdd = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Custom Food", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (customItems.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🍽", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No custom foods yet", color = TextMuted)
                    Text("Tap + to add your own food", color = TextMuted, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(customItems) { item -> FoodSearchItem(item, false) {} }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTextField(label: String, value: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label, fontSize = 11.sp, color = TextMuted) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp), singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (label.contains("(g)") || label == "Calories") KeyboardType.Decimal else KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent, unfocusedBorderColor = BorderDark,
            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = Accent
        )
    )
}

// ── GOALS tab ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsTab(vm: MainViewModel) {
    val profile  by vm.userProfile.collectAsState()
    val targets  by vm.nutritionTargets.collectAsState()
    var weight   by remember { mutableStateOf(profile?.weightKg?.toString() ?: "") }
    var height   by remember { mutableStateOf(profile?.heightCm?.toString() ?: "") }
    var age      by remember { mutableStateOf(profile?.age?.toString() ?: "26") }
    var activity by remember { mutableStateOf(profile?.activityLevel ?: "Moderate") }
    var goal     by remember { mutableStateOf(profile?.goal ?: "Aesthetic") }

    LaunchedEffect(profile) {
        profile?.let {
            weight = it.weightKg.toString()
            height = it.heightCm.toString()
            age = it.age.toString()
            activity = it.activityLevel
            goal = it.goal
        }
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Body Goals & Nutrition", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary) }

        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                border = BorderStroke(1.dp, Gold.copy(0.4f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Your Measurements", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Gold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FoodTextField("Weight (kg)", weight, Modifier.weight(1f)) { weight=it }
                        FoodTextField("Height (cm)", height, Modifier.weight(1f)) { height=it }
                        FoodTextField("Age", age, Modifier.weight(1f)) { age=it }
                    }
                    Text("Activity Level", fontSize = 13.sp, color = TextMuted)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Sedentary","Light","Moderate","Active","VeryActive").forEach { lvl ->
                            val sel = activity == lvl
                            Box(Modifier.clip(RoundedCornerShape(100.dp))
                                .background(if(sel) Gold else Card2Dark)
                                .border(1.dp, if(sel) Gold else BorderDark, RoundedCornerShape(100.dp))
                                .clickable { activity = lvl }.padding(horizontal=10.dp, vertical=5.dp)) {
                                Text(lvl, fontSize=11.sp, color= if(sel) Color.Black else TextMuted, fontWeight= if(sel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                    Text("Goal", fontSize = 13.sp, color = TextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Lose","Maintain","Gain","Aesthetic").forEach { g ->
                            val sel = goal == g
                            Box(Modifier.clip(RoundedCornerShape(100.dp))
                                .background(if(sel) Accent else Card2Dark)
                                .border(1.dp, if(sel) Accent else BorderDark, RoundedCornerShape(100.dp))
                                .clickable { goal = g }.padding(horizontal=10.dp, vertical=5.dp)) {
                                Text(g, fontSize=12.sp, color=if(sel) Color.Black else TextMuted, fontWeight=if(sel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                    Button(
                        onClick = {
                            vm.saveProfile(UserProfile(
                                weightKg = weight.toFloatOrNull() ?: 0f,
                                heightCm = height.toFloatOrNull() ?: 0f,
                                age = age.toIntOrNull() ?: 26,
                                activityLevel = activity, goal = goal
                            ))
                        },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) { Text("Calculate My Goals", color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }
        }

        // Results
        targets?.let { t ->
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                    border = BorderStroke(1.dp, Accent.copy(0.4f))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Your Daily Targets", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Accent)
                        Text(t.summary, color = TextMuted, fontSize = 13.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GoalPill("🔥","${t.calories}","kcal/day", Gold, Modifier.weight(1f))
                            GoalPill("💪","${t.protein}g","protein", Accent, Modifier.weight(1f))
                            GoalPill("🌾","${t.carbs}g","carbs", AccentBlue, Modifier.weight(1f))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GoalPill("🥑","${t.fat}g","fat", Purple, Modifier.weight(1f))
                            GoalPill("🌿","${t.fiber}g","fiber", Color(0xFF4CAF50), Modifier.weight(1f))
                            GoalPill("💧","${t.water.toInt()}L","water", AccentBlue, Modifier.weight(1f))
                        }
                        // BMI + target weight
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Card2Dark).padding(14.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("Current BMI", fontSize = 12.sp, color = TextMuted)
                                Text("%.1f".format(t.bmi), fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                    color = when { t.bmi < 18.5f -> AccentBlue; t.bmi < 25f -> Accent; t.bmi < 30f -> Gold; else -> RedPill })
                                Text(when { t.bmi<18.5f->"Underweight"; t.bmi<25f->"Normal ✅"; t.bmi<30f->"Overweight"; else->"Obese" },
                                    fontSize = 11.sp, color = TextMuted)
                            }
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("Target Weight", fontSize = 12.sp, color = TextMuted)
                                Text("%.1f kg".format(t.targetWeight), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Accent)
                                Text("For aesthetic body (BMI ~22)", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun GoalPill(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(12.dp)).background(color.copy(0.1f))
        .border(1.dp, color.copy(0.3f), RoundedCornerShape(12.dp)).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 16.sp)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}
