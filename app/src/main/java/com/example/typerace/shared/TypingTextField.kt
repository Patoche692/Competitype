package com.example.typerace.shared

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.typerace.settings.PreferencesHelper
import com.example.typerace.training.TrainingViewModelFactory
import com.example.typerace.ui.theme.MonoTypo
import com.example.typerace.ui.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun TypingTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue = TextFieldValue(),
    onValueChange: (TextFieldValue) -> Unit,
    onTextLayout: (TextLayoutResult) -> Unit,
    fontSize: TextUnit = TextUnit.Unspecified,
    maxLines: Int = 4,
    minLines: Int = 4,
    textToType: AnnotatedString,
    requestFocus: Boolean = false,
    onFocusChanged: (FocusState) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    if (requestFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
    Box(modifier = modifier.padding(8.dp)) {
        Text(
            text = textToType,
            fontSize = fontSize,
            lineHeight = (fontSize.value + 2).sp,
            style = MonoTypo.bodyLarge,
            maxLines = maxLines,
            onTextLayout = onTextLayout
        )
        BasicTextField(
            textStyle = MonoTypo.bodyLarge.copy(
                fontSize = fontSize,
                lineHeight = (fontSize.value + 2).sp
            ),
            value = value,
            onValueChange = onValueChange,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
            ),
            modifier = Modifier
                .alpha(0f)
                .onFocusChanged { focusState ->
                    onFocusChanged(focusState)
                }
                .focusRequester(focusRequester)
                .fillMaxWidth()
        )
    }
}

@Composable
fun TypingAnimatedText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = Typography.bodyLarge,
    textColor: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    hapticFeedback: Boolean = true,
    key: Any? = text
) {
    var charIndex by remember{ mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(key1 = key) {
        charIndex = 0
        delay(500)
        while (charIndex < text.length) {
            delay(50)
            charIndex++
            if (hapticFeedback && charIndex % 2 == 0)
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box {
        Text(
            text = text,
            style = textStyle,
            textAlign = textAlign,
            color = Color.Transparent,
            modifier = modifier
        )
        Text(
            text = text.take(charIndex) + if (charIndex < text.length) "\u2B24" else "",
            style = textStyle,
            textAlign = textAlign,
            color = textColor,
            modifier = modifier
        )
    }
}

@Composable
fun InfoCard(modifier: Modifier = Modifier, label: String = "", text: String, icon: Painter) {
    Card(
        modifier = modifier.padding(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Divider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(2.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                )
                Text(
                    text = text,
                    style = Typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (label != "") {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = label,
                    style = Typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun buildAnnotatedText(
    typedText: String,
    textToType: String,
    cursorVisible: Boolean = false
) : AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < typedText.length) {
        append(textToType[i])
        if (typedText[i] != textToType[i]) {
            if(typedText[i] == ' ') {
                addStyle(SpanStyle(MaterialTheme.colorScheme.error.copy(alpha = 0.5f), textDecoration = TextDecoration.Underline), i, i + 1)
            } else {
                addStyle(SpanStyle(MaterialTheme.colorScheme.error), i, i + 1)
            }
        } else {
            addStyle(SpanStyle(MaterialTheme.colorScheme.primary), i, i + 1)
        }
        i++
    }
    if (cursorVisible) {
        addStyle(
            SpanStyle(
                background = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            ),
            i,
            i + 1
        )
    }
    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), i, i + 1)
    append(textToType, i, textToType.length)
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry, factory = TrainingViewModelFactory(PreferencesHelper(LocalContext.current)))
}