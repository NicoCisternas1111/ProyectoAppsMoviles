package com.example.bibliotecaduoc.data.network

import com.example.bibliotecaduoc.model.Book
import retrofit2.Response
import retrofit2.http.*

interface ShozokoApiService {
    @GET("api/books")
    suspend fun getBooks(): List<Book>

    @POST("api/books")
    suspend fun createBook(@Body book: Book): Book

    @PUT("api/books/{id}")
    suspend fun updateBook(@Path("id") id: Long, @Body book: Book): Book

    @DELETE("api/books/{id}")
    suspend fun deleteBook(@Path("id") id: Long): retrofit2.Response<Void>
}