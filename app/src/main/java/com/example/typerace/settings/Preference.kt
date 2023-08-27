package com.example.typerace.settings

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.datastore.preferences.core.Preferences

data class Preference<T>(
    val key: Preferences.Key<T>,
    val icon: ImageVector,
    @StringRes val labelId: Int,
    @StringRes val postLabelId: Int,
    val defaultValue: T,
    val possibleValues: List<T>,
    val onValueChanged: (T) -> Unit,
    val isString: Boolean = false
) {
    var value by mutableStateOf(defaultValue)
    var isFocused by mutableStateOf(false)
}