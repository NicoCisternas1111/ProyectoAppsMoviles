package com.example.bibliotecaduoc.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): BookEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: BookEntity): Long  // <- devuelve el id autogenerado

    @Update
    suspend fun update(entity: BookEntity): Int

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun delete(id: Long): Int
}