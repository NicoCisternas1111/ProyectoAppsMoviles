package com.example.bibliotecaduoc.ui.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ImageUriUtils {

    fun createImageUri(context: Context): Uri {
        val imageDir = File(context.cacheDir, "images").apply {
            if (!exists()) mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(imageDir, "IMG_$timeStamp.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }
}