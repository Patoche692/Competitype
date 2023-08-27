package com.example.typerace.training

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.typerace.settings.PreferencesHelper
import com.example.typerace.shared.sharedViewModel
import com.example.typerace.ui.TypeRaceScreen

fun NavGraphBuilder.trainingGraph(
    navController: NavController,
) {
    navigation(
        startDestination = TypeRaceScreen.TrainingTyping.name,
        route = TypeRaceScreen.Training.name
    ) {
        composable(route = TypeRaceScreen.TrainingTyping.name) {
            val viewModel = viewModel<TrainingViewModel>(factory = TrainingViewModelFactory(
                PreferencesHelper(LocalContext.current)
            ))
            viewModel.focusTextField = false
            TrainingScreen(viewModel)
        }
    }
}
