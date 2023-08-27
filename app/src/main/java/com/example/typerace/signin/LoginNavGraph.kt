package com.example.typerace.signin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.typerace.ui.TypeRaceScreen

fun NavGraphBuilder.loginGraph(navController: NavController, googleAuthUiClient: GoogleAuthUiClient) {
    navigation(
        startDestination = TypeRaceScreen.OneTapSignIn.name,
        route = TypeRaceScreen.Login.name
    ) {
        val onUserSignedIn = {
            navController.navigate(TypeRaceScreen.Training.name) {
                popUpTo(TypeRaceScreen.Login.name) {
                    inclusive = true
                }
            }
        }
        composable(TypeRaceScreen.OneTapSignIn.name) {
            SignInScreen(
                googleAuthUiClient = googleAuthUiClient,
                onUserSignedIn = onUserSignedIn,
            )
        }
    }
}
