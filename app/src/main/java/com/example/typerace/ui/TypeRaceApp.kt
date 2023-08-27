package com.example.typerace.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.typerace.MainActivity
import com.example.typerace.R
import com.example.typerace.multiplayer.MultiplayerScreen
import com.example.typerace.profile.ProfileScreen
import com.example.typerace.settings.PreferencesHelper
import com.example.typerace.shared.NavigationBottomBar
import com.example.typerace.shared.NavigationSideBar
import com.example.typerace.shared.TypeRaceTopAppBar
import com.example.typerace.signin.GoogleAuthUiClient
import com.example.typerace.signin.loginGraph
import com.example.typerace.training.TrainingScreen
import com.example.typerace.training.trainingGraph
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun TypeRaceApp(mainActivity: MainActivity) {

    val context = LocalContext.current
    val activity = context.findActivity()
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = activity.applicationContext,
            oneTapClient = Identity.getSignInClient(activity.applicationContext)
        )
    }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = TypeRaceScreen.valueOf(
        backStackEntry?.destination?.route ?: TypeRaceScreen.OneTapSignIn.name
    )
    val windowsSizeClass = calculateWindowSizeClass(activity)
    val showNavigationRail = windowsSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val showNavigationBar = currentScreen != TypeRaceScreen.OneTapSignIn
    val userSignedIn = googleAuthUiClient.getSignedInUser() != null
    val startDestination = if (userSignedIn) TypeRaceScreen.Training.name else TypeRaceScreen.Login.name

    val navBarItems = listOf(
        NavigationItem(
            title = TypeRaceScreen.Training.name,
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu
        ),
        NavigationItem(
            title = TypeRaceScreen.Multiplayer.name,
            selectedIcon = Icons.Filled.PlayArrow,
            unselectedIcon = Icons.Outlined.PlayArrow
        ),
        NavigationItem(
            title = TypeRaceScreen.Profile.name,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    var selectedNavItemIndex by rememberSaveable { mutableStateOf(0) }

    val onNavigate = { index: Int ->
        navController.navigate(navBarItems[index].title) {
            popUpTo(navBarItems[selectedNavItemIndex].title) {
                inclusive = true
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        selectedNavItemIndex = index
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = if (showNavigationBar && showNavigationRail) 80.dp else 0.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TypeRaceTopAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.popBackStack() },
                    navigateToSettings = {
                    }
                )
            },
            bottomBar = {
                if (showNavigationBar && !showNavigationRail) {
                    NavigationBottomBar(
                        items = navBarItems,
                        selectedItemIndex = selectedNavItemIndex,
                        onNavigate = onNavigate,
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(padding)
            ) {
                loginGraph(navController, googleAuthUiClient)

                trainingGraph(navController)

                composable(route = TypeRaceScreen.Multiplayer.name) {
                    MultiplayerScreen()
                }

                composable(route = TypeRaceScreen.Profile.name) {
                    ProfileScreen(
                        userData = googleAuthUiClient.getSignedInUser(),
                        onSignOut = {
                            mainActivity.lifecycleScope.launch {
                                googleAuthUiClient.signOut()
                                Toast.makeText(
                                    context,
                                    "Signed out",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate(TypeRaceScreen.OneTapSignIn.name) {
                                    popUpTo(TypeRaceScreen.Profile.name) {
                                        inclusive = true
                                        selectedNavItemIndex = 0
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    if (showNavigationBar && showNavigationRail) {
        NavigationSideBar(
            items = navBarItems,
            selectedItemIndex = selectedNavItemIndex,
            onNavigate = onNavigate
        )
    }
}

enum class TypeRaceScreen(@StringRes val title: Int) {
    Training(title = R.string.training),
    TrainingTyping(title = R.string.training),
    TrainingResult(title = R.string.training_result),
    OneTapSignIn(title = R.string.one_tap_sign_in),
    Multiplayer(title = R.string.multiplayer),
    Login(title = R.string.login),
    Profile(title = R.string.profile)
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TypeRaceAppPreview () {
    Scaffold (
        topBar = {
            TypeRaceTopAppBar(
                TypeRaceScreen.Training,
                false,
                {},
                {}
            )
        }
    ) {
        it
        TrainingScreen()
    }
}