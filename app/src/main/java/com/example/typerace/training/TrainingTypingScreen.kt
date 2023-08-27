package com.example.typerace.training

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.typerace.R
import com.example.typerace.shared.InfoCard
import com.example.typerace.shared.MinimalLabeledIconButton
import com.example.typerace.shared.TypingTextField
import com.example.typerace.shared.buildAnnotatedText
import com.example.typerace.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun TrainingTypingScreen(
    viewModel: TrainingViewModel,

) {
    val trainingState by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            InfoCard(
                text = viewModel.remainingTime.toString(),
                icon = painterResource(id = R.drawable.baseline_access_time_24),
                label = stringResource(id = R.string.time) + " (s)"
            )
            InfoCard(
                text = viewModel.wpm.toString(),
                icon = painterResource(id = R.drawable.baseline_speed_24),
                label = stringResource(id = R.string.speed) + " (wpm)"
            )
            InfoCard(
                text = viewModel.accuracy.toString(),
                icon = painterResource(id = R.drawable.point_scan_fill0_wght700_grad0_opsz48),
                label = stringResource(id = R.string.accuracy) + " (%)"
            )
        }
        TypingTextField(
            modifier = Modifier.padding(vertical = 5.dp),
            value = trainingState.typedText,
            onValueChange = {
                viewModel.update(it)
            },
            textToType = buildAnnotatedText(
                typedText = trainingState.typedText.text.substring(viewModel.startIndex),
                textToType = trainingState.textToType.substring(viewModel.startIndex),
                cursorVisible = viewModel.cursorVisible
            ),
            onTextLayout = { textLayoutResult ->
                scope.launch {
                    for (lineNumber in (0 until textLayoutResult.lineCount)) {
                        if (lineNumber < 3) {
                            viewModel.lineEnd[lineNumber] =
                                viewModel.startIndex + textLayoutResult.getLineEnd(lineNumber) - 1
                        }
                    }
                }
            },
            onFocusChanged = { focusState ->
                viewModel.onFocusStateChanged(focusState)
            },
            fontSize = viewModel.preferences[1].value.sp,
            requestFocus = viewModel.focusTextField
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MinimalLabeledIconButton(
                icon = Icons.Rounded.KeyboardArrowRight,
                label = stringResource(id = R.string.new_test),
                onClick = { viewModel.reset(retry = false) }
            )
            MinimalLabeledIconButton(
                icon = Icons.Rounded.Refresh,
                label = stringResource(id = R.string.retry_test),
                onClick = { viewModel.reset(retry = true) }
            )
            AnimatedVisibility (!trainingState.trainingStarted) {
                MinimalLabeledIconButton(
                    icon = Icons.Filled.Settings,
                    label = stringResource(id = R.string.settings),
                    onClick = { viewModel.showSettings = !trainingState.trainingStarted }
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        AnimatedVisibility(visible = !trainingState.trainingStarted) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.training_not_started),
                    style = Typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}