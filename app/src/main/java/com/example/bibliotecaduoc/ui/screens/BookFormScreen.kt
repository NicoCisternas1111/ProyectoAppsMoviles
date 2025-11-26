package com.example.bibliotecaduoc.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bibliotecaduoc.navigation.Route
import com.example.bibliotecaduoc.viewmodel.BookFormViewModel
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.imePadding
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.example.bibliotecaduoc.ui.utils.ImageUriUtils

@Composable
fun BookFormScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    vm: BookFormViewModel = viewModel(factory = BookFormViewModel.factory()),
    bookId: String?
) {
    val ui by vm.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(bookId) {
        vm.loadBook(bookId)
    }

    // Dictado por voz
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!text.isNullOrBlank()) {
                vm.onDescriptionChange(text)
                scope.launch { snackbarHostState.showSnackbar("Dictado agregado a Descripción") }
            }
        }
    }

    val requestAudioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            }
            speechLauncher.launch(intent)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de micrófono denegado") }
        }
    }

    // Cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            vm.clearCoverUri()
            scope.launch { snackbarHostState.showSnackbar("Foto cancelada") }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Portada guardada") }
        }
    }

    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = ImageUriUtils.createImageUri(context)
            vm.setCoverUri(uri.toString())
            takePictureLauncher.launch(uri)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado") }
        }
    }

    // Galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
                vm.setCoverUri(uri.toString())
            } catch (e: SecurityException) {
                e.printStackTrace()
                scope.launch { snackbarHostState.showSnackbar("No se pudo obtener permiso para la imagen") }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        val hasAnyError = ui.errorByField.values.any { it != null }
        AnimatedVisibility(
            visible = hasAnyError,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    "Revisa los campos marcados en rojo",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Título
        OutlinedTextField(
            value = ui.title,
            onValueChange = vm::onTitleChange,
            label = { Text("Título") },
            isError = ui.errorByField["title"] != null,
            supportingText = {
                AnimatedVisibility(
                    visible = ui.errorByField["title"] != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        ui.errorByField["title"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // Autor
        OutlinedTextField(
            value = ui.author,
            onValueChange = vm::onAuthorChange,
            label = { Text("Autor") },
            isError = ui.errorByField["author"] != null,
            supportingText = {
                AnimatedVisibility(
                    visible = ui.errorByField["author"] != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        ui.errorByField["author"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Botones de dictado / cámara / galería (portada)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO) },
                enabled = !ui.isSaving,
                modifier = Modifier.weight(1f)
            ) { Text("Dictar 🎤") }

            OutlinedButton(
                onClick = {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                },
                enabled = !ui.isSaving,
                modifier = Modifier.weight(1f)
            ) { Text("Cámara 📷") }

            OutlinedButton(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                enabled = !ui.isSaving,
                modifier = Modifier.weight(1f)
            ) { Text("Galería 🖼️") }
        }

        // Preview de portada
        if (ui.coverUri != null) {
            Spacer(Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = ui.coverUri,
                    contentDescription = "Portada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Botones guardar / cancelar
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { nav.popBackStack() },
                enabled = !ui.isSaving
            ) { Text("Cancelar") }

            Button(
                onClick = {
                    vm.submit(
                        onSuccess = { id ->
                            val message = if (bookId == null) "Libro creado" else "Libro actualizado"
                            scope.launch { snackbarHostState.showSnackbar(message) }

                            if (bookId == null) {
                                nav.navigate(Route.Details.of(id)) {
                                    popUpTo(Route.Books.path) { inclusive = false }
                                }
                            } else {
                                nav.popBackStack()
                            }
                        },
                        onError = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                },
                enabled = ui.isValid && !ui.isSaving && ui.isDirty,
                modifier = Modifier.animateContentSize()
            ) {
                if (ui.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (bookId == null) "Crear" else "Actualizar")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
