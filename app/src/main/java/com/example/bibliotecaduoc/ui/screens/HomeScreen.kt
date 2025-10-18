package com.example.bibliotecaduoc.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bibliotecaduoc.navigation.Route

@Composable
fun HomeScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { nav.navigate(Route.Books.path) }) { Text("Ver libros") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { nav.navigate(Route.Form.path) }) { Text("Nuevo libro") }
        // (Opcional) Snackbar de prueba:
        // LaunchedEffect(Unit) { snackbarHostState.showSnackbar("Snackbar listo") }
    }
}