package com.example.bibliotecaduoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliotecaduoc.data.network.OrderItemRequest
import com.example.bibliotecaduoc.data.network.OrderRequest
import com.example.bibliotecaduoc.data.network.OrderResponse
import com.example.bibliotecaduoc.data.network.RetrofitClient
import com.example.bibliotecaduoc.data.network.ShozokoApiService
import com.example.bibliotecaduoc.model.Book
import com.example.bibliotecaduoc.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartItem(
    val book: Book,
    val quantity: Int
)

class CartViewModel(
    private val api: ShozokoApiService
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    // Total en pesos del carrito
    val totalPrice: StateFlow<Int> = _items
        .map { list ->
            list.sumOf { it.book.price * it.quantity }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Agregar 1 unidad de un libro al carrito
    fun addToCart(book: Book) {
        val current = _items.value.toMutableList()

        val index = current.indexOfFirst { it.book.id == book.id }
        if (index >= 0) {
            val existing = current[index]
            // No permitir pasar del stock disponible
            if (existing.quantity < book.stock) {
                current[index] = existing.copy(quantity = existing.quantity + 1)
            }
        } else {
            if (book.stock > 0) {
                current.add(CartItem(book = book, quantity = 1))
            }
        }

        _items.value = current
    }

    // Eliminar completamente un libro del carrito
    fun removeFromCart(bookId: String) {
        _items.value = _items.value.filterNot { it.book.id == bookId }
    }

    // Cambiar la cantidad de un libro
    fun updateQuantity(bookId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(bookId)
            return
        }

        _items.value = _items.value.map { item ->
            if (item.book.id == bookId) {
                val maxQty = item.book.stock.coerceAtLeast(0)
                val safeQty = quantity.coerceAtMost(maxQty)
                item.copy(quantity = safeQty)
            } else {
                item
            }
        }
    }

    // Vaciar carrito
    fun clear() {
        _items.value = emptyList()
    }

    // Enviar la compra al backend
    fun checkout(
        onSuccess: (OrderResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val userId = SessionManager.getUserId()
            if (userId == null) {
                onError("Debes iniciar sesión para comprar")
                return@launch
            }

            val currentItems = _items.value
            if (currentItems.isEmpty()) {
                onError("El carrito está vacío")
                return@launch
            }

            // Convertir los libros del carrito a OrderItemRequest
            val itemsReq = mutableListOf<OrderItemRequest>()
            for (ci in currentItems) {
                val longId = ci.book.id.toLongOrNull()
                if (longId == null) {
                    onError("ID de libro inválido: ${ci.book.id}")
                    return@launch
                }
                if (ci.quantity <= 0) continue

                itemsReq.add(
                    OrderItemRequest(
                        bookId = longId,
                        quantity = ci.quantity
                    )
                )
            }

            if (itemsReq.isEmpty()) {
                onError("No hay ítems válidos en el carrito")
                return@launch
            }

            try {
                val request = OrderRequest(
                    userId = userId,
                    items = itemsReq
                )

                val response = api.createOrder(request)

                // Si llegó aquí, el backend ya descontó stock
                clear()
                onSuccess(response)

            } catch (e: Exception) {
                onError("No se pudo completar la compra")
            }
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                CartViewModel(RetrofitClient.api)
            }
        }
    }
}
