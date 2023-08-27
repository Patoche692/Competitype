package com.example.typerace.signin

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInErrorMessage: String? = null,
    val signInButtonClicked: Boolean = false
)
