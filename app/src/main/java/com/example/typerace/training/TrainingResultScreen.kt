package com.example.typerace.training

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.typerace.R
import com.example.typerace.settings.PreferencesHelper
import com.example.typerace.shared.InfoCard
import com.example.typerace.shared.MinimalLabeledIconButton
import com.example.typerace.shared.buildAnnotatedText
import com.example.typerace.ui.theme.MonoTypo
import com.example.typerace.ui.theme.Typography

@Composable
fun TrainingResultScreen(
    viewModel: TrainingViewModel = TrainingViewModel(PreferencesHelper(LocalContext.current)),
    onNewTest: () -> Unit = {},
    onMenu: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    val trainingState by viewModel.state.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(top = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            InfoCard(
                label = stringResource(id = R.string.speed) + " (wpm)",
                text = viewModel.wpm.toString(),
                icon = painterResource(id = R.drawable.baseline_speed_24)
            )
            InfoCard(
                label = stringResource(id = R.string.accuracy) + " (%)", text = viewModel.accuracy.toString(),
                icon = painterResource(id = R.drawable.point_scan_fill0_wght700_grad0_opsz48)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        ResultText(
            textToType = trainingState.textToType,
            typedText = trainingState.typedText.text,
            fontSize = viewModel.preferences[1].value.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MinimalLabeledIconButton(
                icon = Icons.Rounded.KeyboardArrowRight,
                label = stringResource(id = R.string.new_test),
                onClick = onNewTest
            )
            MinimalLabeledIconButton(
                icon = Icons.Rounded.Refresh,
                label = stringResource(id = R.string.retry_test),
                onClick = onRetry
            )
            MinimalLabeledIconButton(
                icon = Icons.Rounded.Warning,
                label = stringResource(id = R.string.practice_missed),
                onClick = onMenu
            )
        }
    }
}

@Composable
fun ResultText(
    modifier: Modifier = Modifier,
    textToType: String,
    typedText: String,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val annotatedText = buildAnnotatedText(typedText = typedText, textToType = textToType.substring(0, typedText.length))
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            //color = MaterialTheme.colorScheme.primary,
            text = stringResource(id = R.string.typed_text),
            style = Typography.labelLarge
        )
        Text(
            text = annotatedText,
            modifier = modifier
                .fillMaxWidth()
                .height(90.dp)
                .verticalScroll(scroll),
            style = MonoTypo.bodyLarge,
            fontSize = fontSize
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview
@Composable
fun ResultScreenPreview() {
    MaterialTheme(dynamicDarkColorScheme(LocalContext.current)) {
        TrainingResultScreen()
    }
}