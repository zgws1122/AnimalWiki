package com.example.code.ui

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Category : Screen("category")
    data object Detail : Screen("detail/{animalId}") {
        fun createRoute(animalId: Int) = "detail/$animalId"
    }
    data object Profile : Screen("profile")
    data object Favorite : Screen("favorite")
    data object History : Screen("history")
    data object Settings : Screen("settings")
    data object Search : Screen("search")
}
