package com.venkat.healthapp

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.google.firebase.firestore.FirebaseFirestore
import com.venkat.healthapp.auth.data.AuthState
import com.venkat.healthapp.auth.ui.AuthScreen
import com.venkat.healthapp.auth.viewmodel.AuthViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.expense.insights.InsightScreen
import com.venkat.healthapp.expense.receipt.ReceiptScreen
import com.venkat.healthapp.expense.receipt.ReceiptViewModel
import com.venkat.healthapp.expense.receipt.ReceiptViewModelFactory
import com.venkat.healthapp.expense.ui.ExpenseScreen
import com.venkat.healthapp.expense.ui.LendBorrowScreen
import com.venkat.healthapp.expense.ui.SplitExpenseScreen
import com.venkat.healthapp.food.ui.FoodScreen
import com.venkat.healthapp.hair.photo.PhotoViewModel
import com.venkat.healthapp.hair.photo.PhotoViewModelFactory
import com.venkat.healthapp.hair.ui.*
import com.venkat.healthapp.home.HomeScreen
import com.venkat.healthapp.water.ui.*
import com.venkat.healthapp.hair.ui.screens.TodayScreen
import com.venkat.healthapp.hair.ui.screens.DashboardScreen
import com.venkat.healthapp.hair.ui.screens.PhotoJournalScreen
import com.venkat.healthapp.hair.ui.screens.AlarmScreen
import com.venkat.healthapp.sleep.ui.SleepScreen
import com.venkat.healthapp.vault.ui.VaultScreen
import com.venkat.healthapp.workout.ui.WorkoutScreen

// ── Nav routes ────────────────────────────────────────────────────────────────
object Routes {
    const val HOME          = "home"
    const val HAIR_MAIN     = "hair_main"
    const val HAIR_TODAY    = "hair_today"
    const val HAIR_DASHBOARD= "hair_dashboard"
    const val HAIR_ALARM    = "hair_alarm"
    const val HAIR_JOURNAL  = "hair_journal"
    const val FOOD          = "food"
    const val WATER         = "water"
    const val SLEEP         = "sleep"
    const val WORKOUT       = "workout"
    const val EXPENSE       = "expense"
    const val LEND_BORROW   = "lend_borrow"
    const val SPLIT         = "split"
    const val VAULT         = "vault"
    const val RECEIPTS      = "receipts"
    const val INSIGHTS      = "insights"




}

data class HairTab(val route: String, val label: String, val icon: ImageVector, val outlinedIcon: ImageVector)

val hairTabs = listOf(
    HairTab(Routes.HAIR_TODAY,     "Today",     Icons.Filled.CheckCircle,         Icons.Outlined.CheckCircle),
    HairTab(Routes.HAIR_DASHBOARD, "Dashboard", Icons.Filled.BarChart,            Icons.Outlined.BarChart),
    HairTab(Routes.HAIR_ALARM,     "Reminders", Icons.Filled.NotificationsActive, Icons.Outlined.NotificationsNone),
    HairTab(Routes.HAIR_JOURNAL,   "Journal",   Icons.Filled.PhotoLibrary,        Icons.Outlined.PhotoLibrary),
)

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel       by viewModels()
    private val authVm: AuthViewModel   by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)


        FirebaseFirestore.getInstance().clearPersistence()
            .addOnCompleteListener {
                Log.d("Firestore", "Cache cleared")
            }

        setContent {
            HealthAppTheme {

                AppRoot(vm = vm, authVm = authVm)

                val photoVm: PhotoViewModel = viewModel(
                    factory = PhotoViewModelFactory(application, vm.db)
                )
//                AppNavHost(vm = vm, photoVm = photoVm)
            }
        }
    }
}

@Composable
fun AppRoot(vm: MainViewModel, authVm: AuthViewModel) {
    val authState by authVm.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            // Splash / loading screen
            Box(
                Modifier.fillMaxSize().background(BgDark),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("💚", fontSize = 64.sp)
                    CircularProgressIndicator(color = Accent)
                    Text("Loading...", color = TextMuted)
                }
            }
        }
        is AuthState.Unauthenticated -> {
            // Show login/register
            AuthScreen(
                authVm    = authVm,
                onLoggedIn = { /* authState will update automatically */ }
            )
        }
        is AuthState.Authenticated -> {
            val user = (authState as AuthState.Authenticated).user
            val context = LocalContext.current  // ← ADD THIS

            LaunchedEffect(user.uid) {
                vm.initSync(user.uid)
            }

            val photoVm: PhotoViewModel = viewModel(
                factory = PhotoViewModelFactory(
                    context.applicationContext as android.app.Application,  // ← FIX THIS
                    vm.db
                )
            )
            AppNavHost(vm = vm, photoVm = photoVm, authVm = authVm)
        }

    }
}

// ── Root nav host ─────────────────────────────────────────────────────────────
@Composable
fun AppNavHost(vm: MainViewModel, photoVm: PhotoViewModel, authVm: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                vm = vm,
                onHairTracker  = { navController.navigate(Routes.HAIR_MAIN) },
                onFoodTracker  = { navController.navigate(Routes.FOOD) },
                onWaterTracker = { navController.navigate(Routes.WATER) },
                onSleepTracker = { navController.navigate(Routes.SLEEP) },
                onWorkoutTracker = { navController.navigate(Routes.WORKOUT) },
                onExpenseTracker = { navController.navigate(Routes.EXPENSE) },
                onVault            = { navController.navigate(Routes.VAULT) },
                onReceiptScanner   = { navController.navigate(Routes.RECEIPTS) },
                onLogout         = {
                    authVm.logout()
                    // authState will automatically change to Unauthenticated
                    // AppRoot will show LoginScreen automatically
                }
            )
        }
        composable(Routes.HAIR_MAIN) {
            HairTrackerHost(vm = vm, photoVm = photoVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.FOOD) {
            FeatureScaffold(title = "Food Tracker", onBack = { navController.popBackStack() }) {
                FoodScreen(vm)
            }
        }
        composable(Routes.WATER) {
            FeatureScaffold(title = "Water Tracker", onBack = { navController.popBackStack() }) {
                WaterScreen(vm)
            }
        }
        composable(Routes.SLEEP) {
            FeatureScaffold(title = "Sleep Tracker", onBack = { navController.popBackStack() }) {
                SleepScreen(vm)
            }
        }

        composable(Routes.WORKOUT) {
            FeatureScaffold(title = "Workout Tracker", onBack = { navController.popBackStack() }) {
                WorkoutScreen(vm)
            }
            }
        composable(Routes.EXPENSE) {
            FeatureScaffold(title = "Expense Tracker", onBack = { navController.popBackStack() }) {
                ExpenseScreen(vm)
            }
        }
        composable(Routes.LEND_BORROW) {
            FeatureScaffold(title = "Money Tracker", onBack = { navController.popBackStack() }) {
                LendBorrowScreen(vm)
            }
        }
        composable(Routes.SPLIT) {
            FeatureScaffold(title = "Split Expense", onBack = { navController.popBackStack() }) {
                SplitExpenseScreen(vm)
            }
        }
        composable(Routes.VAULT) {
            FeatureScaffold(title = "Secure Vault", onBack = { navController.popBackStack() }) {
                VaultScreen(vm)
            }
        }
        composable(Routes.RECEIPTS) {

            val context = LocalContext.current

            val receiptVm: ReceiptViewModel = viewModel(
                factory = ReceiptViewModelFactory(
                    context.applicationContext as Application,
                    vm.db
                )
            )

            FeatureScaffold(
                title = "Receipt Scanner",
                onBack = { navController.popBackStack() }
            ) {
                ReceiptScreen(receiptVm)
            }
        }

        composable(Routes.INSIGHTS) {
            FeatureScaffold(title = "Spending Insights", onBack = { navController.popBackStack() }) {
                InsightScreen(vm)
            }
        }

    }
}

// ── Hair tracker host with its own bottom nav ─────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HairTrackerHost(vm: MainViewModel, photoVm: PhotoViewModel, onBack: () -> Unit) {
    val navController = rememberNavController()
    val navBackStack  by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStack?.destination?.route
    val (done, total) = vm.todayProgress.collectAsState().value

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text("Hair Tracker", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = TextPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardDark)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CardDark, tonalElevation = 0.dp) {
                hairTabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick  = {
                            navController.navigate(tab.route) {
                                popUpTo(Routes.HAIR_TODAY) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        },
                        icon = {
                            Box {
                                Icon(
                                    if (selected) tab.icon else tab.outlinedIcon,
                                    contentDescription = tab.label,
                                    tint = if (selected) Accent else TextMuted
                                )
                                if (tab.route == Routes.HAIR_TODAY && done < total && total > 0) {
                                    Badge(Modifier.align(Alignment.TopEnd), containerColor = RedPill) {
                                        Text("${total - done}", fontSize = 9.sp)
                                    }
                                }
                            }
                        },
                        label = { Text(tab.label, color = if (selected) Accent else TextMuted, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = AccentAlpha)
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController   = navController,
            startDestination = Routes.HAIR_TODAY,
            modifier         = Modifier.padding(padding)
        ) {
            composable(Routes.HAIR_TODAY)     { TodayScreen(vm) }
            composable(Routes.HAIR_DASHBOARD) { DashboardScreen(vm) }
            composable(Routes.HAIR_ALARM)     { AlarmScreen() }
            composable(Routes.HAIR_JOURNAL)   { PhotoJournalScreen(photoVm) }
        }
    }
}

// ── Generic feature scaffold with back button ─────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureScaffold(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = TextPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardDark)
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { content() }
    }
}
