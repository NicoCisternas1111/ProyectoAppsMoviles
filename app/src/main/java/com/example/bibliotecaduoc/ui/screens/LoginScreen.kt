package com.example.bibliotecaduoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecaduoc.data.network.AuthResponse
import com.example.bibliotecaduoc.session.SessionManager
import com.example.bibliotecaduoc.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    onLoginSuccess: (AuthResponse) -> Unit,
    vm: AuthViewModel = viewModel(factory = AuthViewModel.factory)
) {
    val scope = rememberCoroutineScope()
    val error = vm.errorMessage
    val isLoading = vm.isLoading

    // Si ya hay sesión guardada, disparamos onLoginSuccess automáticamente
    LaunchedEffect(Unit) {
        if (SessionManager.isLoggedIn()) {
            val userId = SessionManager.getUserId()
            val name = SessionManager.getUserName() ?: "Usuario"
            val email = SessionManager.getUserEmail() ?: ""
            val role = SessionManager.getUserRole() ?: "USER"

            // Construimos un AuthResponse "fake" desde lo guardado
            val auth = AuthResponse(
                id = userId ?: 0L,
                name = name,
                email = email,
                role = role
            )
            onLoginSuccess(auth)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = vm.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = vm.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    vm.login { auth ->
                        scope.launch {
                            snackbarHostState.showSnackbar("Bienvenido, ${auth.name}")
                        }
                        onLoginSuccess(auth)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Ingresando..." else "Ingresar")
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { /* aquí podrías poner registro si lo implementas luego */ }) {
                Text("¿No tienes cuenta? (opcional implementar registro)")
            }
        }
    }
}
