package com.example.bibliotecaduoc.di

import android.content.Context
import com.example.bibliotecaduoc.data.network.RetrofitClient
import com.example.bibliotecaduoc.repository.BooksRepository
import com.example.bibliotecaduoc.repository.NetworkBooksRepository

object RepositoryProvider {

    @Volatile
    private var _booksRepository: BooksRepository? = null

    fun init(context: Context) {
        if (_booksRepository == null) {
            synchronized(this) {
                if (_booksRepository == null) {
                    _booksRepository = NetworkBooksRepository(RetrofitClient.api)
                }
            }
        }
    }

    val booksRepository: BooksRepository
        get() = _booksRepository
            ?: error("RepositoryProvider no inicializado")
}