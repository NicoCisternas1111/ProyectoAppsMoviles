package com.example.bibliotecaduoc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val author: String,
    val year: Int? = null,          // <-- NUEVO: coincide con el modelo Book
    val description: String = "",   // puede quedar vacío si no se dicta
    val coverUri: String? = null    // URI de la foto tomada con la cámara
)