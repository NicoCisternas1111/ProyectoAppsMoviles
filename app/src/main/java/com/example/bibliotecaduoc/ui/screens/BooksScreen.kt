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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun BooksScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    windowSizeClass: WindowSizeClass,
    vm: BooksViewModel = viewModel(factory = BooksViewModel.factory())
) {
    val books by vm.books.collectAsStateWithLifecycle(initialValue = emptyList())
    val searchQuery by vm.searchQuery.collectAsStateWithLifecycle()
    val isLoading by vm.isLoading.collectAsStateWithLifecycle(initialValue = false)

    val uiState = when {
        isLoading -> "loading"
        books.isEmpty() && searchQuery.isBlank() -> "empty"
        books.isEmpty() && searchQuery.isNotBlank() -> "no_results"
        else -> "content"
    }

    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        floatingActionButton = {
            if (uiState == "content" || uiState == "no_results" || uiState == "empty") {
                FloatingActionButton(
                    onClick = {
                        nav.navigate(Route.Form.path)
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear nuevo libro")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = vm::onSearchQueryChange,
                label = { Text("Buscar por título, autor o año") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                })
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxSize()
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
                                    nav.navigate(Route.Form.path)
                                }) {
                                    Text("Crear primer libro")
                                }
                            }
                        }
                        "no_results" -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No se encontraron resultados", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(8.dp))
                                Text("Intenta con otra palabra clave.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        "content" -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(
                                    items = books,
                                    key = { it.id }
                                ) { b ->
                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { nav.navigate(Route.Details.of(b.id)) },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Portada del libro
                                            BookCoverImage(b.coverUri, b.title)

                                            // Información del libro
                                            Column(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                                    .weight(1f)
                                            ) {
                                                Text(
                                                    text = b.title,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    text = b.author,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                b.year?.let { y ->
                                                    Spacer(Modifier.height(8.dp))
                                                    Text(
                                                        text = y.toString(),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // --- AQUÍ TERMINA EL CAMBIO DE DISEÑO ---
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookCoverImage(coverUri: String?, title: String) {
    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 150.dp)
            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!coverUri.isNullOrBlank()) {
            AsyncImage(
                model = coverUri,
                contentDescription = "Portada de $title",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Book,
                contentDescription = "Sin portada",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}