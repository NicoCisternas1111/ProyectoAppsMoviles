package com.example.bibliotecaduoc.model

data class Book(
    val id: String = "0", 
    val title: String,
    val author: String,
    
    val category: String = "General",
    val price: Int = 0,
    val stock: Int = 0,

    val year: Int? = null,
    val coverUri: String? = null
)