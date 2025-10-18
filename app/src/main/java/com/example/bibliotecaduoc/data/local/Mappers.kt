package com.example.bibliotecaduoc.data.local

import com.example.bibliotecaduoc.model.Book

/**
 * Convierte entre la entidad de base de datos (BookEntity)
 * y el modelo de dominio (Book) que usa la capa de UI.
 */

fun BookEntity.toModel() = Book(
    id = id.toString(),           // Room usa Long, el dominio usa String
    title = title,
    author = author,
    year = year,
    coverUri = coverUri
)

fun Book.toEntity() = BookEntity(
    id = id.toLongOrNull() ?: 0L,  // si viene vacío o UUID, genera uno nuevo
    title = title,
    author = author,
    year = year,
    description = "",              // pendiente: dictado de voz (micrófono)
    coverUri = coverUri
)