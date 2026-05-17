package com.example.code

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.code.ui.AnimalViewModel
import com.example.code.ui.Screen
import com.example.code.ui.auth.AuthViewModel
import com.example.code.ui.auth.LoginScreen
import com.example.code.ui.auth.RegisterScreen
import com.example.code.ui.category.CategoryScreen
import com.example.code.ui.detail.DetailScreen
import com.example.code.ui.favorite.FavoriteScreen
import com.example.code.ui.history.HistoryScreen
import com.example.code.ui.home.HomeScreen
import com.example.code.ui.profile.ProfileScreen
import com.example.code.ui.search.SearchScreen
import com.example.code.ui.settings.SettingsScreen
import com.example.code.ui.theme.CodeTheme

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimalWikiApp()
        }
    }
}

@Composable
fun AnimalWikiApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    val app = navController.context.applicationContext as App
    val animalViewModel: AnimalViewModel = viewModel(
        factory = AnimalViewModel.Factory(app)
    )
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(app)
    )

    // 深色模式状态
    val prefs = remember { context.getSharedPreferences("settings", 0) }
    var isDarkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
    val onDarkModeChange: (Boolean) -> Unit = { dark ->
        isDarkMode = dark
        prefs.edit().putBoolean("dark_mode", dark).apply()
    }

    // 自动登录：检测到已登录状态时跳转到首页
    val currentUser by authViewModel.currentUser.collectAsState()
    LaunchedEffect(currentUser) {
        if (currentUser != null && currentRoute == Screen.Login.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "首页", Icons.Filled.Home),
        BottomNavItem(Screen.Category, "分类", Icons.AutoMirrored.Filled.List),
        BottomNavItem(Screen.Profile, "我的", Icons.Filled.Person)
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }

    CodeTheme(darkTheme = isDarkMode) {
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onGoRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onGoLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = animalViewModel,
                    onAnimalClick = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    onGoSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }
            composable(Screen.Category.route) {
                CategoryScreen(
                    viewModel = animalViewModel,
                    onAnimalClick = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("animalId") { type = NavType.IntType })
            ) { backStackEntry ->
                val animalId = backStackEntry.arguments?.getInt("animalId") ?: return@composable
                DetailScreen(
                    animalId = animalId,
                    viewModel = animalViewModel,
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    animalViewModel = animalViewModel,
                    onGoFavorite = { navController.navigate(Screen.Favorite.route) },
                    onGoHistory = { navController.navigate(Screen.History.route) },
                    onGoSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Favorite.route) {
                val user = authViewModel.currentUser.collectAsState().value ?: return@composable
                FavoriteScreen(
                    userId = user.id,
                    viewModel = animalViewModel,
                    onAnimalClick = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.History.route) {
                val user = authViewModel.currentUser.collectAsState().value ?: return@composable
                HistoryScreen(
                    userId = user.id,
                    viewModel = animalViewModel,
                    onAnimalClick = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onDarkModeChange = onDarkModeChange,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onSwitchAccount = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = animalViewModel,
                    onAnimalClick = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
    } // CodeTheme
}
