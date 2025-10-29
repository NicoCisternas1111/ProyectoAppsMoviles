package com.example.bibliotecaduoc.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.bibliotecaduoc.navigation.Route
import com.example.bibliotecaduoc.viewmodel.BooksViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Image

@Composable
fun BooksScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    windowSizeClass: WindowSizeClass,
    vm: BooksViewModel = viewModel(factory = BooksViewModel.factory())
) {
    val books by vm.books.collectAsStateWithLifecycle(initialValue = emptyList())
    val isLoading by vm.isLoading.collectAsStateWithLifecycle(initialValue = false)

    val uiState = when {
        isLoading -> "loading"
        books.isEmpty() -> "empty"
        else -> "content"
    }

    val listState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            if (uiState == "content") {
                FloatingActionButton(
                    onClick = {
                        nav.navigate(Route.Form.path) // <-- CAMBIO AQUÍ
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear nuevo libro")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Crossfade(targetState = uiState, label = "books-crossfade") { state ->
                when (state) {
                    "loading" -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    "empty" -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Aún no hay libros", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("Crea tu primer libro para comenzar.", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = {
                                nav.navigate(Route.Form.path) // <-- CAMBIO AQUÍ
                            }) {
                                Text("Crear primer libro")
                            }
                        }
                    }
                    "content" -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState
                        ) {
                            items(
                                items = books,
                                key = { it.id }
                            ) { b ->
                                ListItem(
                                    leadingContent = {
                                        if (!b.coverUri.isNullOrBlank()) {
                                            AsyncImage(
                                                model = b.coverUri,
                                                contentDescription = "Portada de ${b.title}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Outlined.Image,
                                                contentDescription = "Sin portada",
                                                modifier = Modifier.size(56.dp)
                                            )
                                        }
                                    },
                                    headlineContent = {
                                        Text(b.title, style = MaterialTheme.typography.titleMedium)
                                    },
                                    supportingContent = {
                                        Text(b.author)
                                    },
                                    trailingContent = {
                                        b.year?.let { y ->
                                            Text(y.toString(), style = MaterialTheme.typography.labelLarge)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { nav.navigate(Route.Details.of(b.id)) }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}