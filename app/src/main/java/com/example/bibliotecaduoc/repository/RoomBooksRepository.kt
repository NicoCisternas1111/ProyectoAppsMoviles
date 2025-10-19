package com.example.bibliotecaduoc.repository

import com.example.bibliotecaduoc.data.local.AppDatabase
import com.example.bibliotecaduoc.data.local.toEntity
import com.example.bibliotecaduoc.data.local.toModel
import com.example.bibliotecaduoc.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomBooksRepository(
    db: AppDatabase
) : BooksRepository {

    private val dao = db.bookDao()

    override val books: Flow<List<Book>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    override suspend fun getById(id: String): Book? {
        val longId = id.toLongOrNull() ?: return null
        return dao.getById(longId)?.toModel()
    }

    override suspend fun insert(book: Book): String {
        val entity = book.toEntity()
        val generatedId = dao.insert(entity)
        return generatedId.toString()
    }

    override suspend fun update(book: Book): Boolean {
        val rows = dao.update(book.toEntity())
        return rows > 0
    }

    override suspend fun delete(id: String): Boolean {
        val longId = id.toLongOrNull() ?: return false
        val rows = dao.delete(longId)
        return rows > 0
    }
}