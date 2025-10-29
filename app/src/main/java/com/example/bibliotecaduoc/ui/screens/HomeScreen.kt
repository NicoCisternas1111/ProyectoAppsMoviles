package com.example.bibliotecaduoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido a tu Biblioteca", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { nav.navigate(Route.Form.path) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuevo libro")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { nav.navigate(Route.Books.path) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver mis libros")
        }
    }
}