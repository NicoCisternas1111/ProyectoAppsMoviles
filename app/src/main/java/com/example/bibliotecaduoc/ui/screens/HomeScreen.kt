package com.example.bibliotecaduoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import com.example.bibliotecaduoc.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
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
        Text(
            "Bienvenido a tu Biblioteca Digital",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        // 🔹 ÚNICA opción para el usuario: ver todos los libros
        ElevatedCard(
            onClick = { nav.navigate(Route.Books.path) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoStories,
                    contentDescription = "Ver libros",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    "Ver todos los libros",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
