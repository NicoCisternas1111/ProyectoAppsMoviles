package com.example.bibliotecaduoc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bibliotecaduoc.navigation.Route
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.foundation.shape.RoundedCornerShape

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
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))
        ElevatedCard(
            onClick = { nav.navigate(Route.Form.path) },
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
                    imageVector = Icons.Outlined.AddComment,
                    contentDescription = "Nuevo Libro",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    "Nuevo libro",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(Modifier.height(20.dp))
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
                    contentDescription = "Ver mis libros",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    "Ver mis libros",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}