package com.example.bibliotecaduoc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val author: String,
    val year: Int? = null,
    val description: String = "",
    val coverUri: String? = null
)