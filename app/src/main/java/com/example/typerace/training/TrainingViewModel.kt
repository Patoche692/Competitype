package com.example.typerace.training

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.typerace.R
import com.example.typerace.settings.Preference
import com.example.typerace.settings.PreferencesHelper
import com.example.typerace.settings.PreferencesKeys
import com.example.typerace.words.WordsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import kotlin.math.round
import kotlin.random.Random

@Suppress("UNCHECKED_CAST")
class TrainingViewModelFactory(private val preferencesHelper: PreferencesHelper) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TrainingViewModel(preferencesHelper) as T
}

class TrainingViewModel(private val preferencesHelper: PreferencesHelper): ViewModel() {
    private var cursorCoroutine: Job = Job()
    private var timerCoroutine: Job = Job()

    val preferences = listOf(
        Preference(
            key = PreferencesKeys.testDuration,
            icon = Icons.Rounded.Info,
            labelId = R.string.test_duration,
            postLabelId = R.string.seconds,
            defaultValue = 30,
            possibleValues = listOf(15, 30, 60, 120),
            onValueChanged = { duration ->
                remainingTime = duration
            }
        ),
        Preference(
            key = PreferencesKeys.fontSize,
            icon = Icons.Rounded.Menu,
            labelId = R.string.font_size,
            postLabelId = R.string.sp,
            defaultValue = 26,
            possibleValues = listOf(22, 26, 30, 35),
            onValueChanged = { _ ->
            }
        ),
        Preference(
            key = PreferencesKeys.language,
            icon = Icons.Rounded.Place,
            labelId = R.string.language,
            defaultValue = R.string.english,
            postLabelId = R.string.empty,
            possibleValues = listOf(R.string.french, R.string.english, R.string.german, R.string.spanish),
            onValueChanged = { language ->
                wordsManager.updateLanguage(language)
                rand = Random(seed)
                buildText()
            },
            isString = true
        )
    )

    private val wordsManager = WordsManager(R.string.english)
    var focusTextField  = false

    private val _state = MutableStateFlow(TrainingState())
    var remainingTime by mutableStateOf( 30)
    var gameEnded by mutableStateOf(false)
    var wpm by mutableStateOf(0)
    var accuracy by mutableStateOf(100)
    var cursorVisible by mutableStateOf(true)
    var showSettings by mutableStateOf(false)
    private var charsTypedTotal: Int = 0
    private var wrongCharsTotal: Int = 0
    private var rightCharsWPM: Int = 0
    private var currentWordMistakes: Int = 0
    private var seed: Int = Random.nextInt()
    private var rand: Random
    var startIndex = 0
    var lineEnd: Array<Int> = arrayOf(0, 0, 0)
    private var currentLine: Int = 0
    val state = _state.asStateFlow()

    init {
        rand = Random(seed)
        loadPreferences()
    }

    private fun buildText() {
        _state.update {
            it.copy(
                textToType = wordsManager.randomWords(100, rand)
            )
        }
    }

    fun update(newTextFieldValue: TextFieldValue) {
        if (gameEnded) return

        resetCursor()
        updateCursor()

        focusTextField = true

        val currentText = _state.value.typedText.text
        val newText = newTextFieldValue.text

        if (!_state.value.trainingStarted) {
            startAndUpdateTimer()
            _state.update { it.copy(trainingStarted = true) }
        }

        val commonPrefix = newText.commonPrefixWith(currentText)
        val textToDelete = currentText.removePrefix(commonPrefix)
        val textToProcess = newText.removePrefix(commonPrefix)

        deleteText(textToDelete)
        processText(textToProcess)
    }

    private fun deleteText(textToDelete: String) {
        var currentText = _state.value.typedText.text
        val textToType = _state.value.textToType
        lastWord(textToDelete).forEachIndexed { i, char ->
            //Log.d("bonjour", "index : " + i.toString() + " typedChar : " + char + " normal char : " + textToType[currentText.length - textToDelete.length + i])
            if (textToType[currentText.length - textToDelete.length + i] != char) {
                currentWordMistakes--
                if (currentWordMistakes == 0) {
                    rightCharsWPM += lastWord(currentText).length - 1
                }
            }
            else {
                rightCharsWPM--
            }
        }
        currentText = currentText.dropLast(lastWord(textToDelete).length)
        _state.update {
            it.copy(
                typedText = TextFieldValue(currentText, TextRange(currentText.length))
            )
        }
    }

    private fun processText(textToProcess: String) {
        for (char in textToProcess) {
            val typedText = _state.value.typedText.text
            val textToType = _state.value.textToType

            if (char == '\n') {
                reset()
                return
            }
            if (char == ' ') {
                if (lastWord(typedText) == "") {
                    continue
                }

                if (currentWordMistakes > 0 || textToType[typedText.length] != ' ')
                    wrongCharsTotal++
                else
                    rightCharsWPM++

                charsTypedTotal++
                reachNextWord()
            }
            else if (textToType[typedText.length] == ' ') {
                wrongCharsTotal++
                charsTypedTotal++
            }
            else {
                charsTypedTotal++
                if (_state.value.textToType[typedText.length] == char) {
                    if (currentWordMistakes == 0)
                        rightCharsWPM++
                    //if (!isCurrentWordWrong)
                }
                else {
                    if (currentWordMistakes == 0){
                        rightCharsWPM -= lastWord(typedText).length
                    }
                    currentWordMistakes++
                    wrongCharsTotal++
                }
                _state.update {
                    it.copy(
                        typedText = TextFieldValue(typedText + char, TextRange(typedText.length + 1))
                    )
                }
            }
        }
    }

    private fun reachNextWord() {
        var newTypedText = _state.value.typedText.text
        while (_state.value.textToType[newTypedText.length] != ' ')
            newTypedText += ' '
        newTypedText += ' '

        if (newTypedText.length >= lineEnd[currentLine]) {
            if (currentLine == 1)
                return removeFirstLine(newTypedText)
            currentLine = 1
        }

        _state.update {
            it.copy(
                typedText = TextFieldValue(newTypedText, TextRange(newTypedText.length))
            )
        }
        currentWordMistakes = 0
    }

    private fun removeFirstLine(string: String) {
        // We must keep the same number of words after removing the first line
        val numberOfWordsInFirstLine: Int = _state.value.textToType.substring(0, lineEnd[0] + 1).count { it == ' ' }
        val newTextToType: String = _state.value.textToType.plus(wordsManager.randomWords(numberOfWordsInFirstLine, rand))
        currentLine = 1
        // Manually update because recomposition doesn't have an order
        //lineEnd[1] = lineEnd[2] - lineEnd[0]
        startIndex = lineEnd[0] + 1
        lineEnd[0] = lineEnd[1]
        lineEnd[1] = lineEnd[2]

        _state.update {
            it.copy(
                textToType = newTextToType,
                typedText = TextFieldValue(string, TextRange(string.length))
            )
        }
    }

    private fun startAndUpdateTimer() {
        timerCoroutine = viewModelScope.launch {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--
                wpm = calculateWpm()
                accuracy = calculateAccuracy()
            }
            gameEnded = true
        }
    }

    private fun resetCursor(visible: Boolean = true) {
        cursorCoroutine.cancel()
        cursorVisible = visible
    }

    private fun updateCursor() {
        cursorCoroutine = viewModelScope.launch {
            while (true) {
                delay(500)
                cursorVisible = !cursorVisible
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> setPreference(
        preference: Preference<T>,
        value: Any
    ) {
        viewModelScope.launch {
            preferencesHelper.putPreference(preference.key, value as T)
        }
    }

    private fun <T>loadPreference(preference: Preference<T>) {
        viewModelScope.launch {
            preferencesHelper.getPreference(preference.key, preference.defaultValue).collect {
                preference.onValueChanged(it)
                preference.value = it
            }
        }
    }
    private fun loadPreferences() {
        for (preference in preferences) {
            loadPreference(preference = preference)
        }
    }

    private fun calculateWpm(): Int = if (remainingTime == preferences[0].value) 0
    else round(rightCharsWPM.toFloat() / 5.0f / ((preferences[0].value.toFloat() - remainingTime.toFloat()) / 60.0f)).toInt()

    private fun calculateAccuracy(): Int = round((1 - wrongCharsTotal.toFloat() / charsTypedTotal.toFloat()) * 100).toInt()

    private fun nextWord() : String = _state.value.textToType.drop(_state.value.typedText.text.length)
        .takeWhile { char -> char != ' ' }

    private fun lastWord(string: String) : String {
        return string.takeLastWhile { it != ' ' }
    }

    private fun removeLastWord(string: String) : String {
        return string.dropLastWhile { it != ' ' }
    }

    fun reset(
        retry: Boolean = false
    ) {
        resetState()
        if (!retry) {
            seed = Random.nextInt()
        }
        rand = Random(seed)
        charsTypedTotal = 0
        wrongCharsTotal = 0
        rightCharsWPM = 0
        wpm = 0
        gameEnded = false
        currentWordMistakes = 0
        startIndex = 0
        lineEnd = arrayOf(0, 0, 0)
        currentLine = 0
        accuracy = 100
        remainingTime = preferences[0].value as Int
        timerCoroutine.cancel()
        buildText()
    }
    private fun resetState() {
        _state.update { TrainingState() }
    }

    fun onFocusStateChanged(focusState: FocusState) {
        resetCursor(visible = focusState.isFocused)
        if (focusState.isFocused) {
            updateCursor()
        }
    }
}