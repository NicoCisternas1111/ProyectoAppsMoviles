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
import java.time.Year

data class BookFormUiState(
    val title: String = "",
    val author: String = "",
    val year: String = "",
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
        if (id == null) {
            currentBookId = null
            _uiState.value = BookFormUiState()
            return
        }

        currentBookId = id
        viewModelScope.launch {
            val book = repo.getById(id)
            if (book != null) {
                _uiState.update {
                    it.copy(
                        title = book.title,
                        author = book.author,
                        year = book.year?.toString() ?: "",
                        description = "", // O carga la descripción si la guardas
                        coverUri = book.coverUri,
                        isDirty = false
                    )
                }
                validate()
            }
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

    fun onYearChange(value: String) {
        _uiState.update { it.copy(year = value, isDirty = true) }
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

        if (s.title.isBlank()) errs["title"] = "El título es obligatorio"
        else if (s.title.length < 2) errs["title"] = "Mínimo 2 caracteres"

        if (s.author.isBlank()) errs["author"] = "El autor es obligatorio"
        else if (s.author.length < 2) errs["author"] = "Mínimo 2 caracteres"

        val yearErr = validateYear(s.year)
        if (yearErr != null) errs["year"] = yearErr

        if (s.description.isNotBlank() && s.description.length < 5) {
            errs["description"] = "Si agregas descripción, usa al menos 5 caracteres"
        }

        val valid = errs.values.all { it == null } &&
                s.title.isNotBlank() &&
                s.author.isNotBlank() &&
                s.year.isNotBlank()

        _uiState.update { it.copy(errorByField = errs, isValid = valid) }
    }

    private fun validateYear(value: String): String? {
        if (value.isBlank()) return "El año es obligatorio"
        val yr = value.toIntOrNull() ?: return "Debe ser numérico"
        val current = Year.now().value
        val min = 1400
        val max = current + 1
        return if (yr in min..max) null else "Debe estar entre $min y $max"
    }

    fun submit(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val s = _uiState.value
        if (!s.isValid || s.isSaving) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val yearInt = s.year.toIntOrNull()

                if (currentBookId == null) {
                    // --- CREAR ---
                    val book = Book(
                        id = "",
                        title = s.title.trim(),
                        author = s.author.trim(),
                        year = yearInt,
                        coverUri = s.coverUri
                    )
                    val id = repo.insert(book)
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess(id)
                    _uiState.value = BookFormUiState()

                } else {
                    // --- ACTUALIZAR ---
                    val book = Book(
                        id = currentBookId!!,
                        title = s.title.trim(),
                        author = s.author.trim(),
                        year = yearInt,
                        coverUri = s.coverUri
                    )
                    repo.update(book)
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess(currentBookId!!)
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