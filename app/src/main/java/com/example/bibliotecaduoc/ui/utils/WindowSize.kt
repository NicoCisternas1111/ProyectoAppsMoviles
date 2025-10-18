package com.example.bibliotecaduoc.ui.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

fun columnsForWidth(windowSizeClass: WindowSizeClass): Int {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium  -> 2
        WindowWidthSizeClass.Expanded -> 3
        else -> 1
    }
}
