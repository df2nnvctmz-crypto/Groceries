package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.bills.BillsScreen
import com.example.ui.bills.ScanReceiptScreen
import com.example.ui.bills.ScanResultScreen
import com.example.ui.details.FoodDetailSheet
import com.example.ui.home.HomeScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.search.SearchScreen
import com.example.ui.swaps.SwapsScreen
import com.example.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector, val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Today", Icons.Filled.Home, Icons.Outlined.Home)
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Swaps : Screen("swaps", "Swaps", Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz)
    object Bills : Screen("bills", "Bills", Icons.Filled.Receipt, Icons.Outlined.Receipt)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

val items = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Swaps,
    Screen.Bills
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState.selectedFood?.let { food ->
        val swapFood = viewModel.getSwapsFor(food).firstOrNull()
        FoodDetailSheet(
            food = food,
            swapFood = swapFood,
            onDismiss = { viewModel.selectFood(null) },
            onSwapClick = { viewModel.selectFood(it) },
            isFavorite = uiState.favoriteFoodIds.contains(food.id),
            onFavoriteToggle = { viewModel.toggleFavorite(food.id) }
        )
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                NavigationBar(
                    modifier = Modifier.clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp)),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (currentDestination?.hierarchy?.any { it.route == screen.route } == true)
                                        screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = com.example.ui.theme.GreenPrimary.copy(alpha = 0.1f),
                                selectedIconColor = com.example.ui.theme.GreenPrimary,
                                selectedTextColor = com.example.ui.theme.GreenPrimary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    userProfile = uiState.userProfile,
                    spotlightFood = uiState.allFoods.find { it.name.contains("Berliner") } ?: uiState.allFoods.firstOrNull(),
                    recommendedFoods = viewModel.getRecommendedFoods(),
                    onFoodClick = { viewModel.selectFood(it) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    bills = uiState.bills,
                    favoriteFoodIds = uiState.favoriteFoodIds,
                    favoriteSwapIds = uiState.favoriteSwapIds,
                    allFoods = uiState.allFoods,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onToggleSwapFavorite = { from, to -> viewModel.toggleSwapFavorite(from, to) }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.search(it) },
                    searchFilter = uiState.searchFilter,
                    onFilterChange = { viewModel.updateSearchFilter(it) },
                    searchResults = uiState.displayedFoods,
                    loadedFoodsCount = uiState.loadedFoodsCount,
                    onFoodClick = { viewModel.selectFood(it) },
                    onLoadMore = { viewModel.loadMoreFoods() },
                    allCategories = uiState.allFoods.groupingBy { it.category }.eachCount().entries.sortedByDescending { it.value }.take(7).map { it.key }.sorted(),
                    allSubcategories = uiState.allFoods.filter { it.subcategory.isNotBlank() }.groupingBy { it.subcategory }.eachCount().entries.sortedByDescending { it.value }.take(7).map { it.key }.sorted(),
                    favoriteFoodIds = uiState.favoriteFoodIds,
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            }
            composable(Screen.Swaps.route) {
                SwapsScreen(
                    badFoods = uiState.allFoods.filter { it.nutriGrade == "D" || it.nutriGrade == "E" },
                    getSwapsFor = { viewModel.getSwapsFor(it) },
                    onFoodClick = { viewModel.selectFood(it) },
                    favoriteFoodIds = uiState.favoriteFoodIds,
                    favoriteSwapIds = uiState.favoriteSwapIds,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onToggleSwapFavorite = { from, to -> viewModel.toggleSwapFavorite(from, to) }
                )
            }
            composable(Screen.Bills.route) {
                BillsScreen(
                    bills = uiState.bills,
                    allFoods = uiState.allFoods,
                    onDeleteBill = { viewModel.deleteBill(it) },
                    onFoodClick = { food ->
                        viewModel.selectFood(food)
                    },
                    onScanClick = { navController.navigate("scan_receipt") }
                )
            }
            composable("scan_receipt") {
                ScanReceiptScreen(
                    onDemoReceiptClick = { viewModel.generateDemoReceipt(); navController.navigate("scan_result") },
                    onNavigateBack = { navController.popBackStack() },
                    onImageSelected = { uri ->
                        viewModel.scanReceipt(uri)
                        navController.navigate("scan_result")
                    }
                )
            }
            composable("scan_result") {
                ScanResultScreen(
                    scannedItems = uiState.scannedItems,
                    allFoods = uiState.allFoods,
                    onUpdateItem = { index, food -> viewModel.updateScannedItem(index, food) },
                    isScanning = uiState.isScanningReceipt,
                    onNavigateBack = { navController.popBackStack() },
                    onRemoveItem = { viewModel.removeScannedItem(it) },
                    onSaveBill = {
                        viewModel.saveScannedBill()
                        navController.popBackStack(Screen.Bills.route, inclusive = false)
                        viewModel.clearScannedItems()
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    profile = uiState.userProfile,
                    onProfileChange = { viewModel.updateUserProfile(it) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
