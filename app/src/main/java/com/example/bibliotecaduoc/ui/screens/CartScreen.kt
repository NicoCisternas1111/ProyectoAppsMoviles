package com.example.bibliotecaduoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bibliotecaduoc.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    cartVm: CartViewModel
) {
    val items by cartVm.items.collectAsState()
    val total by cartVm.totalPrice.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { ci ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = ci.book.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Cantidad: ${ci.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Precio unitario: $${ci.book.price}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Subtotal: $${ci.book.price * ci.quantity}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = {
                                    cartVm.updateQuantity(ci.book.id, ci.quantity - 1)
                                }
                            ) {
                                Text("-")
                            }
                            TextButton(
                                onClick = {
                                    cartVm.updateQuantity(ci.book.id, ci.quantity + 1)
                                }
                            ) {
                                Text("+")
                            }
                            TextButton(
                                onClick = {
                                    cartVm.removeFromCart(ci.book.id)
                                }
                            ) {
                                Text("Quitar")
                            }
                        }
                    }
                    Divider()
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Total: $$total",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        cartVm.checkout(
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Compra realizada con éxito")
                                }
                                nav.popBackStack()
                            },
                            onError = { msg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    },
                    enabled = items.isNotEmpty()
                ) {
                    Text("Comprar")
                }
            }
        }
    }
}
