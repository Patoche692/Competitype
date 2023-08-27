package com.example.typerace.training

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.typerace.R
import com.example.typerace.settings.PreferencesHelper
import com.example.typerace.shared.InfoCard
import com.example.typerace.shared.MinimalLabeledIconButton
import com.example.typerace.shared.SmallLabeledIconButton
import com.example.typerace.shared.TypeRaceTopAppBar
import com.example.typerace.shared.TypingTextField
import com.example.typerace.shared.buildAnnotatedText
import com.example.typerace.ui.TypeRaceScreen
import com.example.typerace.ui.theme.Typography
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrainingScreen(
    viewModel: TrainingViewModel = viewModel(factory = TrainingViewModelFactory(
        PreferencesHelper(LocalContext.current)
    ))
) {

    AnimatedContent(
        targetState = viewModel.gameEnded,
        label = "",
        transitionSpec = {
            fadeIn(animationSpec = tween(300, delayMillis = 150)) with fadeOut(animationSpec = tween(300))
        }
    ) { visible ->
        if (!visible) {
            TrainingTypingScreen(viewModel = viewModel)
        }
        else {
            TrainingResultScreen(viewModel = viewModel, onRetry = { viewModel.reset(retry = true) }, onNewTest = { viewModel.reset(retry = false) })
        }

    }
    if (viewModel.showSettings) {
        SettingsBottomSheet(
            viewModel = viewModel,
            onDismissRequest = { viewModel.showSettings = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    viewModel: TrainingViewModel
) {
    val sheetState = rememberModalBottomSheetState(true)
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            viewModel.preferences.forEach { preference ->
                val valueText = if (!preference.isString) preference.value
                else stringResource(id = preference.value)
                    
                SmallLabeledIconButton(
                    modifier = Modifier.padding(10.dp),
                    icon = preference.icon,
                    label = stringResource(id = preference.labelId) +
                            " : " + valueText + " " +
                            stringResource(id = preference.postLabelId),
                    onClick = {
                        var show = true
                        viewModel.preferences.forEach {
                            if (it.isFocused)
                                show = false
                        }
                        preference.isFocused = show
                    }
                )
                if (preference.isFocused) {
                    Dialog(onDismissRequest = { preference.isFocused = false }) {
                        Column(
                            modifier = Modifier
                                .width(IntrinsicSize.Max)
                                .clip(
                                    RoundedCornerShape(10)
                                )
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp)
                        ) {
                            preference.possibleValues.forEachIndexed { index, value ->
                                Row(
                                    verticalAlignment = CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.setPreference(preference, value)
                                            preference.isFocused = false
                                        }
                                ) {
                                    Icon(
                                        preference.icon,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .size(30.dp),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    val text = if (!preference.isString) value.toString() + " " + stringResource(preference.postLabelId)
                                    else stringResource(id = preference.possibleValues[index]) + " " + stringResource(id = preference.postLabelId)
                                    Text(text = text, modifier = Modifier.padding(15.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TrainingScreenPreview() {
    MaterialTheme(dynamicDarkColorScheme(LocalContext.current)) {
        Scaffold(
            topBar = {
                TypeRaceTopAppBar(
                    currentScreen = TypeRaceScreen.Training,
                    canNavigateBack = true,
                    navigateUp = {},
                    navigateToSettings = {}
                )
            }
        ) {
            it
            TrainingScreen()
        }
    }
}