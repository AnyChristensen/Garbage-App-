package dk.christensen.garbagefinalassignment


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dk.christensen.garbagefinalassignment.core.NotificationHelper
import dk.christensen.garbagefinalassignment.core.workers.EmptyBinNotificationWorker
import dk.christensen.garbagefinalassignment.core.workers.WelcomeNotificationWorker
import dk.christensen.garbagefinalassignment.domain.Theme
import dk.christensen.garbagefinalassignment.ui.Features.settings.SettingsViewModel
import dk.christensen.garbagefinalassignment.ui.components.GlobalCountdownBanner
import dk.christensen.garbagefinalassignment.ui.components.RequestNotificationPermission
import dk.christensen.garbagefinalassignment.ui.navigation.MainNavigation
import dk.christensen.garbagefinalassignment.ui.theme.GarbageFinalTheme
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.remember
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val welcomeWorkRequest = OneTimeWorkRequestBuilder<WelcomeNotificationWorker>()
            .setInitialDelay(duration = 1, timeUnit = TimeUnit.MINUTES)
            .build()

        // KEEP means: "If a job named 'WelcomeWork' is already scheduled, ignore this request."
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            uniqueWorkName = "WelcomeWork",
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            request = welcomeWorkRequest
        )

        val emptyBinWorkRequest = PeriodicWorkRequestBuilder<EmptyBinNotificationWorker>(
            repeatInterval = 1, repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            uniqueWorkName = "DailyEmptyBinCheck",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = emptyBinWorkRequest
        )



        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = when (uiState.theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            // We call enableEdgeToEdge here, inside setContent, to recompose when the theme changes.
            // This ensures the status and navigation bar colors update dynamically.
            // The SystemBarStyle.auto() constructor requires us to explicitly provide the scrim
            // colors. We are using the library's default values to maintain standard behavior.
            enableEdgeToEdge(
                statusBarStyle = androidx.activity.SystemBarStyle.auto(
                    lightScrim = android.graphics.Color.TRANSPARENT,
                    darkScrim = android.graphics.Color.TRANSPARENT,
                ) { darkTheme },
                navigationBarStyle = androidx.activity.SystemBarStyle.auto(
                    lightScrim = android.graphics.Color.argb(
                        /* alpha = */0xe6,
                        /* red = */ 0xFF,
                        /* green = */ 0xFF,
                        /* blue = */ 0xFF
                    ),
                    darkScrim = android.graphics.Color.argb(
                        /* alpha = */ 0x80,
                        /* red = */ 0x1b,
                        /* green = */ 0x1b,
                        /* blue = */ 0x1b
                    ),
                ) { darkTheme },
            )
            GarbageFinalTheme {


                val mainViewModel: MainViewModel = hiltViewModel()
                val remainingMillis by mainViewModel.remainingMillis.collectAsState()
                val isBannerVisible = remainingMillis > 0


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    Column(modifier = Modifier.fillMaxSize()) {

                        if (isBannerVisible) {
                            GlobalCountdownBanner(remainingMillis = remainingMillis)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                // If the banner is showing, we tell the TopAppBar
                                // "Don't add status bar padding, the banner already handled it!"
                                .let {
                                    if (isBannerVisible) {
                                        it.consumeWindowInsets(WindowInsets.statusBars)
                                    } else {
                                        it
                                    }
                                }
                        ) {
                            MainNavigation()
                        }
                    }
                }


            }
        }
    }
}
