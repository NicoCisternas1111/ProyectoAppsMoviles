package com.example.bibliotecaduoc.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val year: Int? = null,
    val coverUri: String? = null
)