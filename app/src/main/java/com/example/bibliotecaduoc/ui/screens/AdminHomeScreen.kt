package com.example.bibliotecaduoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bibliotecaduoc.navigation.Route

@Composable
fun AdminHomeScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel de administrador",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { nav.navigate(Route.Books.path) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gestionar libros")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                // Más adelante: ir a pantalla de usuarios cuando la creemos
                // nav.navigate(Route.Users.path)  // cuando exista
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gestionar usuarios (próximamente)")
        }
    }
}
