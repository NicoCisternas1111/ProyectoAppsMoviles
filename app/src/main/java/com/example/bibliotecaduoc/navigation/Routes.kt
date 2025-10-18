package com.example.bibliotecaduoc.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Books : Routes("books")
    data object BookForm : Routes("book_form")
    data object BookDetails : Routes("book_details/{id}") {
        fun create(id: Long) = "book_details/$id"
    }
}