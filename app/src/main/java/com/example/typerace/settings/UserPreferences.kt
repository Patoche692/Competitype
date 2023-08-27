package com.example.typerace.settings

import androidx.compose.ui.unit.TextUnit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val fontSize = intPreferencesKey("font_size")
    val testDuration = intPreferencesKey("test_duration")
    val language = intPreferencesKey("language")
}
