package com.example.bibliotecaduoc.di

import android.content.Context
import com.example.bibliotecaduoc.data.local.AppDatabase
import com.example.bibliotecaduoc.repository.BooksRepository
import com.example.bibliotecaduoc.repository.InMemoryBooksRepository
import com.example.bibliotecaduoc.repository.RoomBooksRepository

object RepositoryProvider {

    @Volatile
    private var _booksRepository: BooksRepository? = null

    fun init(context: Context) {
        if (_booksRepository == null) {
            synchronized(this) {
                if (_booksRepository == null) {
                    val db = AppDatabase.build(context)
                    _booksRepository = RoomBooksRepository(db)
                }
            }
        }
    }

    val booksRepository: BooksRepository
        get() = _booksRepository
            ?: error("RepositoryProvider no inicializado. Llama a init(context) primero.")
}