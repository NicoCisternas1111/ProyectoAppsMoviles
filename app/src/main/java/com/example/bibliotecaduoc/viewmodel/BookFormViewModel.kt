package com.example.bibliotecaduoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bibliotecaduoc.di.RepositoryProvider
import com.example.bibliotecaduoc.model.Book
import com.example.bibliotecaduoc.repository.BooksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookFormUiState(
    val title: String = "",
    val author: String = "",
    val category: String = "",
    val price: String = "",
    val stock: String = "",
    val description: String = "",
    val coverUri: String? = null,
    val errorByField: Map<String, String?> = emptyMap(),
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val isDirty: Boolean = false
)

class BookFormViewModel(
    private val repo: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookFormUiState())
    val uiState: StateFlow<BookFormUiState> = _uiState.asStateFlow()

    private var currentBookId: String? = null

    fun loadBook(id: String?) {
        if (id == null) return

        viewModelScope.launch {
            val book = repo.getById(id) ?: return@launch
            currentBookId = book.id

            _uiState.update {
                it.copy(
                    title = book.title,
                    author = book.author,
                    category = book.category,
                    price = if (book.price > 0) book.price.toString() else "",
                    stock = if (book.stock > 0) book.stock.toString() else "",
                    description = "",          // puedes rellenar si algún día lo guardas
                    coverUri = book.coverUri,
                    isDirty = false
                )
            }
            validate()
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, isDirty = true) }
        validate()
    }

    fun onAuthorChange(value: String) {
        _uiState.update { it.copy(author = value, isDirty = true) }
        validate()
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value, isDirty = true) }
        validate()
    }

    fun onPriceChange(value: String) {
        _uiState.update { it.copy(price = value, isDirty = true) }
        validate()
    }

    fun onStockChange(value: String) {
        _uiState.update { it.copy(stock = value, isDirty = true) }
        validate()
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, isDirty = true) }
        validate()
    }

    fun setCoverUri(uri: String?) {
        _uiState.update { it.copy(coverUri = uri, isDirty = true) }
        validate()
    }

    fun clearCoverUri() {
        _uiState.update { it.copy(coverUri = null, isDirty = true) }
        validate()
    }

    private fun validate() {
        val s = _uiState.value
        val errs = mutableMapOf<String, String?>()

        errs["author"] = if (s.author.isBlank()) "El autor es obligatorio" else null
        errs["category"] = if (s.category.isBlank()) "La categoría es obligatoria" else null

        // precio
        errs["price"] = when {
            s.price.isBlank() -> "El precio es obligatorio"
            s.price.toIntOrNull() == null -> "Debe ser numérico"
            s.price.toInt() < 0 -> "Debe ser un número positivo"
            else -> null
        }

        // stock
        errs["stock"] = when {
            s.stock.isBlank() -> "El stock es obligatorio"
            s.stock.toIntOrNull() == null -> "Debe ser numérico"
            s.stock.toInt() < 0 -> "Debe ser un número positivo"
            else -> null
        }

        // título
        errs["title"] = if (s.title.isBlank()) "El título es obligatorio" else null

        // descripción opcional
        if (s.description.isNotBlank() && s.description.length < 5) {
            errs["description"] = "Si agregas descripción, usa al menos 5 caracteres"
        }

        val valid = errs.values.all { it == null } &&
                s.title.isNotBlank() &&
                s.author.isNotBlank() &&
                s.category.isNotBlank() &&
                s.price.isNotBlank() &&
                s.stock.isNotBlank()

        _uiState.update { it.copy(errorByField = errs, isValid = valid) }
    }

    fun submit(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val s = _uiState.value
        if (!s.isValid || s.isSaving) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val priceInt = s.price.toIntOrNull() ?: 0
                val stockInt = s.stock.toIntOrNull() ?: 0

                if (currentBookId == null) {
                    // CREAR
                    val book = Book(
                        id = "",
                        title = s.title.trim(),
                        author = s.author.trim(),
                        category = s.category.trim(),
                        price = priceInt,
                        stock = stockInt,
                        year = null,              // 👈 año ya no se usa
                        coverUri = s.coverUri
                    )
                    val id = repo.insert(book)
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess(id)
                    _uiState.value = BookFormUiState()
                } else {
                    // ACTUALIZAR
                    val book = Book(
                        id = currentBookId!!,
                        title = s.title.trim(),
                        author = s.author.trim(),
                        category = s.category.trim(),
                        price = priceInt,
                        stock = stockInt,
                        year = null,              // 👈 mantenemos null
                        coverUri = s.coverUri
                    )
                    repo.update(book)
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess(book.id)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                onError("No se pudo guardar: ${e.message ?: "Error desconocido"}")
            }
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = RepositoryProvider.booksRepository
                return BookFormViewModel(repo) as T
            }
        }
    }
}
