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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecaduoc.ui.screens.AdminHomeScreen
import com.example.bibliotecaduoc.ui.screens.BookDetailsScreen
import com.example.bibliotecaduoc.ui.screens.BookFormScreen
import com.example.bibliotecaduoc.ui.screens.BooksScreen
import com.example.bibliotecaduoc.ui.screens.CartScreen
import com.example.bibliotecaduoc.ui.screens.HomeScreen
import com.example.bibliotecaduoc.ui.screens.LoginScreen
import com.example.bibliotecaduoc.data.network.AuthResponse
import com.example.bibliotecaduoc.session.SessionManager
import com.example.bibliotecaduoc.viewmodel.CartViewModel

sealed class Route(val path: String) {
    data object Login  : Route("login")
    data object Home   : Route("home")
    data object Admin  : Route("admin")
    data object Books  : Route("books")
    data object Cart   : Route("cart")
    data object Details : Route("details/{id}") {
        fun of(id: String) = "details/$id"
    }
    data object Form   : Route("bookForm")
    data object Edit   : Route("bookEdit/{id}") {
        fun of(id: String) = "bookEdit/$id"
    }
}

private fun titleFor(route: String?): String = when {
    route == null -> "Biblioteca"
    route.startsWith(Route.Login.path) -> "Iniciar sesión"
    route.startsWith(Route.Home.path) -> "Inicio"
    route.startsWith(Route.Admin.path) -> "Panel admin"
    route.startsWith(Route.Books.path) -> "Libros"
    route.startsWith(Route.Cart.path) -> "Carrito"
    route.startsWith("details/") -> "Detalle"
    route.startsWith(Route.Form.path) -> "Nuevo libro"
    route.startsWith("bookEdit/") -> "Editar libro"
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

    // CartViewModel compartido
    val cartVm: CartViewModel = viewModel(factory = CartViewModel.factory)

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
                },
                actions = {
                    // 🔹 Mostrar "Cerrar sesión" en todas las pantallas menos Login
                    if (currentRoute != null && !currentRoute.startsWith(Route.Login.path)) {
                        TextButton(
                            onClick = {
                                // Limpiar sesión + carrito
                                SessionManager.logout()
                                cartVm.clear()

                                // Navegar a login limpiando el backstack
                                nav.navigate(Route.Login.path) {
                                    popUpTo(nav.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Text("Cerrar sesión")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Route.Login.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Login
            composable(Route.Login.path) {
                LoginScreen(
                    snackbarHostState = snackbarHostState,
                    onLoginSuccess = { auth: AuthResponse ->
                        val isAdmin = auth.role.equals("ADMIN", ignoreCase = true)
                        val target = if (isAdmin) Route.Admin.path else Route.Home.path

                        nav.navigate(target) {
                            popUpTo(Route.Login.path) { inclusive = true }
                        }
                    }
                )
            }

            // Home (USER)
            composable(Route.Home.path) {
                HomeScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState
                )
            }

            // Panel admin
            composable(Route.Admin.path) {
                AdminHomeScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState
                )
            }

            // Libros
            composable(Route.Books.path) {
                BooksScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState,
                    windowSizeClass = windowSizeClass,
                    cartVm = cartVm
                )
            }

            // Carrito
            composable(Route.Cart.path) {
                CartScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState,
                    cartVm = cartVm
                )
            }

            // Detalle
            composable(
                route = Route.Details.path,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                BookDetailsScreen(
                    nav = nav,
                    id = id,
                    snackbarHostState = snackbarHostState
                )
            }

            // Crear libro
            composable(Route.Form.path) {
                BookFormScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState,
                    bookId = null
                )
            }

            // Editar libro
            composable(
                route = Route.Edit.path,
                arguments = listOf(navArgument("id") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                BookFormScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState,
                    bookId = id
                )
            }
        }
    }
}
