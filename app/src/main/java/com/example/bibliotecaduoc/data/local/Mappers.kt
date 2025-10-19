package com.example.bibliotecaduoc.data.local

import com.example.bibliotecaduoc.model.Book


fun BookEntity.toModel() = Book(
    id = id.toString(),
    title = title,
    author = author,
    year = year,
    coverUri = coverUri
)

fun Book.toEntity() = BookEntity(
    id = id.toLongOrNull() ?: 0L,
    title = title,
    author = author,
    year = year,
    description = "",
    coverUri = coverUri
)