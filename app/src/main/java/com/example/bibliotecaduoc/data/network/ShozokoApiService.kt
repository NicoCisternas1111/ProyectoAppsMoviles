package com.example.bibliotecaduoc.data.network

import com.example.bibliotecaduoc.model.Book
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// 🔹 Modelos para autenticación contra el backend
data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String    // "ADMIN" o "USER"
)

// 🔹 Modelos para crear órdenes (carrito)
data class OrderItemRequest(
    val bookId: Long,
    val quantity: Int
)

data class OrderRequest(
    val userId: Long,
    val items: List<OrderItemRequest>
)

data class OrderResponse(
    val id: Long,
    val userId: Long
)

interface ShozokoApiService {

    // ===== Libros =====

    @GET("api/books")
    suspend fun getBooks(): List<Book>

    @POST("api/books")
    suspend fun createBook(@Body book: Book): Book

    @PUT("api/books/{id}")
    suspend fun updateBook(
        @Path("id") id: Long,
        @Body book: Book
    ): Book

    @DELETE("api/books/{id}")
    suspend fun deleteBook(
        @Path("id") id: Long
    ): Response<Void>

    // ===== Autenticación =====

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    // ===== Órdenes (Carrito) =====

    @POST("api/orders")
    suspend fun createOrder(
        @Body request: OrderRequest
    ): OrderResponse
}
