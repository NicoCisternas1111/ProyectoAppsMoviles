package com.example.bibliotecaduoc.repository

import com.example.bibliotecaduoc.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class InMemoryBooksRepository : BooksRepository {
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    override val books = _books.asStateFlow()

    override suspend fun getById(id: String): Book? =
        _books.value.firstOrNull { it.id == id }

    override suspend fun insert(book: Book): String {
        val id = if (book.id.isBlank()) UUID.randomUUID().toString() else book.id
        val toInsert = book.copy(id = id)
        _books.update { current -> current + toInsert }
        return id
    }

    override suspend fun update(book: Book): Boolean {
        var updated = false
        _books.update { current ->
            val idx = current.indexOfFirst { it.id == book.id }
            if (idx >= 0) {
                updated = true
                current.toMutableList().apply { set(idx, book) }
            } else current
        }
        return updated
    }

    override suspend fun delete(id: String): Boolean {
        var removed = false
        _books.update { current ->
            val before = current.size
            val after = current.filterNot { it.id == id }
            removed = after.size != before
            after
        }
        return removed
    }
}