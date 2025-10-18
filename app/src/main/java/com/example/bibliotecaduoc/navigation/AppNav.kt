package com.example.bibliotecaduoc.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.bibliotecaduoc.ui.screens.BookDetailsScreen
import com.example.bibliotecaduoc.ui.screens.BookFormScreen
import com.example.bibliotecaduoc.ui.screens.BookSummaryScreen
import com.example.bibliotecaduoc.ui.screens.BooksScreen
import com.example.bibliotecaduoc.ui.screens.HomeScreen

// Rutas tipadas (usa tus mismas rutas)
sealed class Route(val path: String) {
    data object Home    : Route("home")
    data object Books   : Route("books")
    data object Details : Route("details/{id}") {
        fun of(id: String) = "details/$id"
    }
    data object Form    : Route("bookForm")
    data object Summary : Route("bookSummary")
}

private fun titleFor(route: String?): String = when {
    route == null -> "Biblioteca"
    route.startsWith(Route.Home.path) -> "Inicio"
    route.startsWith(Route.Books.path) -> "Libros"
    route.startsWith("details/") -> "Detalle"
    route.startsWith(Route.Form.path) -> "Nuevo libro"
    route.startsWith(Route.Summary.path) -> "Resumen"
    else -> "Biblioteca"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(windowSizeClass: WindowSizeClass) {
    val nav = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val canNavigateBack = nav.previousBackStackEntry != null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(titleFor(currentRoute)) },
                navigationIcon = {
                    if (canNavigateBack && currentRoute != Route.Home.path) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Route.Home.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.path) {
                HomeScreen(nav = nav, snackbarHostState = snackbarHostState)
            }
            composable(Route.Books.path) {
                BooksScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState,
                    windowSizeClass = windowSizeClass
                )
            }
            composable(
                route = Route.Details.path,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                BookDetailsScreen(nav = nav, id = id, snackbarHostState = snackbarHostState)
            }
            composable(Route.Form.path) {
                BookFormScreen(nav = nav, snackbarHostState = snackbarHostState)
            }
            composable(Route.Summary.path) {
                BookSummaryScreen(nav = nav, snackbarHostState = snackbarHostState)
            }
        }
    }
}