package com.example.bibliotecaduoc.repository

import kotlinx.coroutines.flow.Flow
import com.example.bibliotecaduoc.model.Book

interface BooksRepository {
    val books: Flow<List<Book>>

    suspend fun getById(id: String): Book?

    suspend fun insert(book: Book): String

    suspend fun update(book: Book): Boolean

    suspend fun delete(id: String): Boolean
}