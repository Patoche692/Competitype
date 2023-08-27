package com.example.typerace.training

import androidx.compose.ui.text.input.TextFieldValue

data class TrainingState(
    val textToType: String = "",
    val typedText: TextFieldValue = TextFieldValue(),
    val trainingStarted: Boolean = false
)

data class Word(
    val string: String,
    var typingMistake: Boolean
)

enum class Entry {
    SKIPPED,
    RIGHT,
    WRONG
}