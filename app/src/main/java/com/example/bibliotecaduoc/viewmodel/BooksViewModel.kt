package com.example.bibliotecaduoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bibliotecaduoc.di.RepositoryProvider
import com.example.bibliotecaduoc.model.Book
import com.example.bibliotecaduoc.repository.BooksRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BooksViewModel(
    private val repo: BooksRepository
) : ViewModel() {

    // Exponemos el flujo del repo directamente (UI lo observará)
    val books: StateFlow<List<Book>> =
        repo.books.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Si quisieras simular una carga inicial:
        // viewModelScope.launch {
        //     _isLoading.value = true
        //     delay(400)
        //     _isLoading.value = false
        // }
    }

    fun addBook(book: Book, onInserted: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.insert(book)
            onInserted(id)
        }
    }

    suspend fun getBookById(id: String): Book? = repo.getById(id)

    fun updateBook(book: Book, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onDone(repo.update(book))
        }
    }

    fun deleteBook(id: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onDone(repo.delete(id))
        }
    }

    companion object {
        /** Factory local para inyectar repo sin Hilt */
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = RepositoryProvider.booksRepository
                return BooksViewModel(repo) as T
            }
        }
    }
}