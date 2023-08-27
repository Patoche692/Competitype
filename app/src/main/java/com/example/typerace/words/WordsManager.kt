package com.example.typerace.words

import androidx.annotation.StringRes
import com.example.typerace.R
import org.intellij.lang.annotations.Language
import kotlin.random.Random

class WordsManager(@StringRes private var language: Int) {
    private var wordsList = CommonWords.English

    init {
        updateLanguage(language)
    }

    fun randomWords(numberOfWords: Int, rand: Random): String {
        var words = ""
        for (i in (1..numberOfWords)) {
            words += wordsList[rand.nextInt(199)] + " "
        }
        return words
    }

    fun updateLanguage(@StringRes language: Int) {
        this.language = language
        wordsList = when(language) {
            R.string.french -> CommonWords.French
            R.string.english -> CommonWords.English
            R.string.spanish -> CommonWords.Spanich
            R.string.german -> CommonWords.German
            else -> CommonWords.French
        }
    }
}