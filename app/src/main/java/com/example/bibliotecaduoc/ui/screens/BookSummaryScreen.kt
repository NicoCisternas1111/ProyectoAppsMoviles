package com.example.bibliotecaduoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.SnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSummaryScreen(nav: NavController, snackbarHostState: SnackbarHostState) {
    Scaffold(topBar = {CenterAlignedTopAppBar(title = { Text("Resumen") }) }) { pad ->
        Text("Resumen (lo completamos en el Paso 6)", modifier = Modifier.padding(pad).padding(16.dp))
    }
}