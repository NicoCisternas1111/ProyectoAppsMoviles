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
import com.example.bibliotecaduoc.viewmodel.CartViewModel
import com.example.bibliotecaduoc.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun BooksScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    windowSizeClass: WindowSizeClass,
    cartVm: CartViewModel,
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
    val scope = rememberCoroutineScope()

    val isAdmin = SessionManager.getUserRole()
        ?.equals("ADMIN", ignoreCase = true) == true

    Scaffold(
        floatingActionButton = {
            if (isAdmin && (uiState == "content" || uiState == "no_results" || uiState == "empty")) {
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

            Spacer(Modifier.height(8.dp))

            // Botón para ir al carrito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { nav.navigate(Route.Cart.path) }) {
                    Text("Ver carrito")
                }
            }

            Spacer(Modifier.height(8.dp))

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
                                Text(
                                    "Aún no hay libros",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                if (isAdmin) {
                                    Text(
                                        "Crea tu primer libro para comenzar.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Button(onClick = {
                                        nav.navigate(Route.Form.path)
                                    }) {
                                        Text("Crear primer libro")
                                    }
                                } else {
                                    Text(
                                        "Contacta a un administrador para que agregue libros.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        "no_results" -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No se encontraron resultados",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Intenta con otra palabra clave.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
                                            .clickable {
                                                nav.navigate(Route.Details.of(b.id))
                                            },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Portada
                                            BookCoverImage(b.coverUri, b.title)

                                            Spacer(Modifier.width(12.dp))

                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = b.title,
                                                    style = MaterialTheme.typography.titleMedium,
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

                                                Spacer(Modifier.height(8.dp))

                                                Text(
                                                    text = "$${b.price}",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )

                                                Spacer(Modifier.height(4.dp))

                                                if (b.stock <= 0) {
                                                    Text(
                                                        text = "Agotado",
                                                        color = MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.labelMedium
                                                    )
                                                } else {
                                                    Text(
                                                        text = "Stock: ${b.stock}",
                                                        style = MaterialTheme.typography.labelMedium
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.width(8.dp))

                                            val isOutOfStock = b.stock <= 0

                                            Button(
                                                onClick = {
                                                    if (!isOutOfStock) {
                                                        cartVm.addToCart(b)
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                "Agregado al carrito: ${b.title}"
                                                            )
                                                        }
                                                    }
                                                },
                                                enabled = !isOutOfStock
                                            ) {
                                                Text(
                                                    text = if (isOutOfStock) "Agotado" else "Agregar"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
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
