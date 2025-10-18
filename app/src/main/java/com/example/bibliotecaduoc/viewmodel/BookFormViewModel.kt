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

// ===============================
// UiState centralizada (Opción A)
// ===============================
data class BookFormUiState(
    val title: String = "",
    val author: String = "",
    val year: String = "",                // lo mantenemos como String para validar amigable en UI
    val description: String = "",         // (UI) listo para dictado; hoy no se persiste en Book
    val coverUri: String? = null,         // reservado para Cámara/Galería
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

    // ===========================
    // Handlers de cambio de campos
    // ===========================
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

    // ===========================
    // Portada (Cámara/Galería)
    // ===========================
    fun setCoverUri(uri: String?) {
        _uiState.update { it.copy(coverUri = uri, isDirty = true) }
        // Si en el futuro quieres que la portada sea obligatoria:
        // agrega una regla en validate() que marque error si coverUri == null
        validate()
    }

    fun clearCoverUri() {
        _uiState.update { it.copy(coverUri = null, isDirty = true) }
        validate()
    }

    // ==================================
    // Validación centralizada por campos
    // ==================================
    private fun validate() {
        val s = _uiState.value
        val errs = mutableMapOf<String, String?>()

        // Título
        if (s.title.isBlank()) errs["title"] = "El título es obligatorio"
        else if (s.title.length < 2) errs["title"] = "Mínimo 2 caracteres"

        // Autor
        if (s.author.isBlank()) errs["author"] = "El autor es obligatorio"
        else if (s.author.length < 2) errs["author"] = "Mínimo 2 caracteres"

        // Año
        val yearErr = validateYear(s.year)
        if (yearErr != null) errs["year"] = yearErr

        // (Opcional) Descripción: ejemplo de regla mínima
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

    // =========================================
    // Submit: inserta y devuelve id vía callbacks
    // =========================================
    fun submit(onInserted: (String) -> Unit, onError: (String) -> Unit) {
        val s = _uiState.value
        if (!s.isValid || s.isSaving) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val yearInt = s.year.toIntOrNull()
                val book = Book(
                    id = "",
                    title = s.title.trim(),
                    author = s.author.trim(),
                    year = yearInt,
                    coverUri = s.coverUri // listo para cámara/galería
                )
                val id = repo.insert(book)
                _uiState.update { it.copy(isSaving = false) }
                onInserted(id)

                // limpiar formulario tras guardar
                _uiState.value = BookFormUiState()

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