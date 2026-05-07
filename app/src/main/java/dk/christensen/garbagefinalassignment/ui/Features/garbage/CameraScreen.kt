package dk.christensen.garbagefinalassignment.ui.Features.garbage

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import dk.christensen.garbagefinalassignment.R
import dk.christensen.garbagefinalassignment.ui.components.GarbageTopAppBar
import dk.christensen.garbagefinalassignment.ui.components.NavigationType
import dk.christensen.garbagefinalassignment.ui.navigation.AppRoute
import kotlinx.serialization.Serializable
import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.guava.await
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.util.concurrent.Executors


@Serializable
object CameraScreen : AppRoute

@Composable
fun CameraScreen(
    onNavigate: (CameraViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    // to collect the navigation events from the viewmodel
    LaunchedEffect(Unit) {viewModel.navigationEvents.collect {onNavigate(it)}}

    CameraScreen(
        uiState = uiState,
        uiEvents = viewModel.uiEvents,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
private fun CameraScreen(
    uiState: CameraViewModel.UiState,
    uiEvents: CameraViewModel.UiEvents,
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State to hold the camera permission (granted or not)
    var hasCameraPermission by remember { mutableStateOf(false) }

    // To remember PreviewView so that is does not recreate (rerender) if the view (object) recompositions
    val previewView = remember { PreviewView(context) }

    // Launcher used to request permission to use the camera from the OS and update state
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted
        }

    // Request camera permission when screen opens
    LaunchedEffect(Unit) {

        permissionLauncher.launch(
            Manifest.permission.CAMERA
        )
    }

    Scaffold(
        topBar = {
            GarbageTopAppBar(
                titleRes = R.string.title_screen,
                navigationType = NavigationType.BACK,
                onUpClick = viewModel.uiEvents::onUpClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Only display camera preview if the permission is granted
            if (hasCameraPermission) {

                // Displays the live camera preview on screen
                // Embedded in AndroidView to be used in compose
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                        //.height(500.dp)
                        .weight(1f)
                )

                // Start CameraX after permission has been granted
                LaunchedEffect(hasCameraPermission) {

                    startCamera(
                        context,
                        lifecycleOwner,
                        previewView,
                        viewModel
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Displaying the result from the models detection result
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val detectedItem = uiState.detectedItem

                    // The different text messages during scanning
                    // Since the model is not that good at scanning I want to show:
                    // 1. What the model labels (what it "sees"), when it does not have a match with the database ("Detected")
                    // 2. When the model is "scanning"
                    // 3. When there is a match between the models label and a garbage item form the database
                    // (4. Where it should be disposed, if matched)
                    when {


                        // When searching the database
                        uiState.isScanning -> {

                            Text(
                                text = "Scanning...",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                text = "Detected: ${uiState.detectedLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        // When a match is found
                        detectedItem != null -> {

                            Text(
                                text = "Found garbage: ${detectedItem.what}",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                text = "Dispose in: ${detectedItem.where}",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Label found but no database match
                        uiState.detectedLabel != null -> {

                            Text(
                                text = "Detected: ${uiState.detectedLabel}",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                text = "No recycling category found",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Initial state is relevant when using emulator (since noting in the picture is garbage)
                        else -> {

                            Text(
                                text = "Point camera at garbage item",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }


                    }
                }
            }
        }
    }
}


// Function to start the camera and bind it to the screens lifecycle
// Build on the function on slide 19 (lecture 10)
private suspend fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView, // The UI Surface
    viewModel: CameraViewModel
) {

    // CameraProvider is the main CameraX entry point
    val cameraProvider = ProcessCameraProvider.getInstance(context).await()

    // 1. Define the Use Case:
    // "I want to see the camera on screen" (Defining the preview use case)
    val previewUseCase = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }

    // 2. Define the analysis Use Case:
    // "I want to analyze frames for ML"
    val analysisUseCase = ImageAnalysis.Builder().build()

    // Running the ML analysis on a background thread to prevent it from running on the main thread
    val cameraExecutor = Executors.newSingleThreadExecutor()

    // On main thread
    // val cameraExecutor = ContextCompat.getMainExecutor(context)

    // Connecting the CameraX frames with the GarbageAnalyzer
    analysisUseCase.setAnalyzer(
        cameraExecutor,
        GarbageAnalyzer {detectedLabel ->

            // Forwarding the labels from the model (ML) to the ViewModel
            viewModel.uiEvents.onLabelDetected(detectedLabel)
        }
    )

    try {
        // 3. Unbind old sessions, bind new ones to the Lifecycle
        cameraProvider.unbindAll()
        // Binding the camera (cameraX) to the app's lifecycle such that is "closes" when the screen dismantles
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            previewUseCase,
            analysisUseCase
        )
    } catch(e: Exception) {
        Log.e("Camera", "Hardware binding failed", e)
    }
}



