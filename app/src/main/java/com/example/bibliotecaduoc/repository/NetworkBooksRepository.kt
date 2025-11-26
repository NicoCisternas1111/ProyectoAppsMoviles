package com.example.bibliotecaduoc.repository

import com.example.bibliotecaduoc.data.network.ShozokoApiService
import com.example.bibliotecaduoc.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkBooksRepository(
    private val api: ShozokoApiService
) : BooksRepository {

    override val books: Flow<List<Book>> = flow {
        emit(api.getBooks())
    }

    override suspend fun getById(id: String): Book? {
        val longId = id.toLongOrNull() ?: return null
        return api.getBooks().find { it.id == id } 
    }

    override suspend fun insert(book: Book): String {
        val savedBook = api.createBook(book)
        return savedBook.id
    }

    override suspend fun update(book: Book): Boolean {
        val longId = book.id.toLongOrNull() ?: return false
        return try {
            api.updateBook(longId, book)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun delete(id: String): Boolean {
        val longId = id.toLongOrNull() ?: return false
        val response = api.deleteBook(longId)
        return response.isSuccessful
    }
}