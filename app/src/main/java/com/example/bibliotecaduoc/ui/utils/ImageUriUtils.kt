package com.example.bibliotecaduoc.ui.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Genera URIs seguras para guardar imágenes tomadas con la cámara
 * usando FileProvider.
 *
 * Guarda las fotos temporalmente en /cache/images/
 */
object ImageUriUtils {

    fun createImageUri(context: Context): Uri {
        // Crear carpeta temporal
        val imageDir = File(context.cacheDir, "images").apply {
            if (!exists()) mkdirs()
        }

        // Crear archivo con nombre único (fecha/hora)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(imageDir, "IMG_$timeStamp.jpg")

        // Devolver URI segura a través del FileProvider
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }
}