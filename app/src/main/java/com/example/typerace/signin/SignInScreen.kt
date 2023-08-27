package com.example.typerace.signin

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.typerace.MainActivity
import com.example.typerace.R
import com.example.typerace.shared.LabeledIconButton
import com.example.typerace.shared.SmallLabeledIconButton
import com.example.typerace.shared.TypingAnimatedText
import com.example.typerace.ui.theme.Typography
import kotlinx.coroutines.launch
import kotlin.math.sign

@Composable
fun SignInScreen(
    googleAuthUiClient: GoogleAuthUiClient,
    onUserSignedIn: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = viewModel<SignInViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if(result.resultCode == RESULT_OK) {
                viewModel.viewModelScope.launch{
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                }
            }
            else if (result.resultCode == RESULT_CANCELED) {
                viewModel.updateSignInState(signInButtonClicked = false)
            }
        }
    )

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if(state.isSignInSuccessful) {
            Toast.makeText(
                context,
                "Sign in successful",
                Toast.LENGTH_LONG
            ).show()
            onUserSignedIn()
        }
    }
    LaunchedEffect(key1 = state.signInErrorMessage) {
        state.signInErrorMessage?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
            viewModel.updateSignInState(signInButtonClicked = false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.weight(0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (state.signInButtonClicked) {
                    LinearProgressIndicator()
                    Text(
                        text = stringResource(id = R.string.welcome_message),
                        style = Typography.titleLarge,
                        color = Color.Transparent,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                } else {
                    TypingAnimatedText(
                        text = stringResource(id = R.string.welcome_message),
                        textStyle = Typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
            SmallLabeledIconButton(
                icon = painterResource(id = R.drawable.google__g__logo),
                label = stringResource(id = R.string.one_tap_sign_in),
                onClick = {
                    if (!state.signInButtonClicked) {
                        viewModel.updateSignInState(signInButtonClicked = true)
                        viewModel.viewModelScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    }
                },
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }
}
