package com.example.bibliotecaduoc.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bibliotecaduoc.data.network.AuthResponse
import com.example.bibliotecaduoc.data.network.LoginRequest
import com.example.bibliotecaduoc.data.network.RetrofitClient
import com.example.bibliotecaduoc.data.network.ShozokoApiService
import com.example.bibliotecaduoc.session.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val api: ShozokoApiService
) : ViewModel() {

    // 🔹 Estado de los campos del formulario
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    // 🔹 Estado de carga y error
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- Handlers para el formulario ---

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    // --- Lógica de login ---

    fun login(
        onSuccess: (AuthResponse) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val request = LoginRequest(
                    email = email.trim(),
                    password = password
                )

                val response = api.login(request)

                // Guardamos la sesión en SharedPreferences
                SessionManager.saveUser(response)

                // Devolvemos el usuario a quien llame al ViewModel
                onSuccess(response)

            } catch (e: Exception) {
                // Por ahora mensaje genérico
                errorMessage = "Correo o contraseña incorrectos"
            } finally {
                isLoading = false
            }
        }
    }

    companion object {
        // Factory para usar este ViewModel desde composables
        val factory = viewModelFactory {
            initializer {
                AuthViewModel(RetrofitClient.api)
            }
        }
    }
}
