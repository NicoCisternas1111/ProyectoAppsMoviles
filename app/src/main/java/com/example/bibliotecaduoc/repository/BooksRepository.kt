package com.example.bibliotecaduoc.repository

import kotlinx.coroutines.flow.Flow
import com.example.bibliotecaduoc.model.Book

interface BooksRepository {
    /** Flujo reactivo con todos los libros (orden que definas) */
    val books: Flow<List<Book>>

    /** Obtiene un libro por id (null si no existe) */
    suspend fun getById(id: String): Book?

    /** Inserta y retorna el id generado (si no lo pasas tú) */
    suspend fun insert(book: Book): String

    /** Actualiza (true si encontró y actualizó) */
    suspend fun update(book: Book): Boolean

    /** Elimina por id (true si existía y eliminó) */
    suspend fun delete(id: String): Boolean
}