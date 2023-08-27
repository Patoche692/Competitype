package com.example.typerace.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.sign

class SignInViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInErrorMessage = result.errorMessage
        ) }
    }

    fun updateSignInState(signInButtonClicked: Boolean) {
        _state.update { it.copy(
            signInButtonClicked = signInButtonClicked
        ) }
    }

    fun resetSate() {
        _state.update { SignInState() }
    }
}