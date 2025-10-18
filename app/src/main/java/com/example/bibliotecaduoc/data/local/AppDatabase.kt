package com.example.bibliotecaduoc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos principal de la app.
 * Contiene la tabla de libros y maneja la persistencia local.
 */
@Database(
    entities = [BookEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Construye o devuelve una instancia singleton de la base de datos.
         * Se destruye y recrea automáticamente si hay cambios de versión
         * (gracias a fallbackToDestructiveMigration()).
         */
        fun build(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}