package dk.christensen.garbagefinalassignment.ui.Features.garbage

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

// The GarbageAnalyzer receives live view/frames from CameraX
// It the runs ML Kit (on device Machine Learning)
// Inspired by the BarcodeAnalyzer on slide 29 (lecture 10)

class GarbageAnalyzer (private val onLabelDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    // Instantiating the ML Kit Model (pre-trained)
    // A model that returns a label that matches what the model detects on the frame (based on its training)
    private val imageLabeler = ImageLabeling.getClient(
        ImageLabelerOptions.DEFAULT_OPTIONS
    )

    // Used to limit how often the ML Kit runs (analyze the frames)
    private var lastAnalyzedTime = 0L

    // Creating the analyzer function to analyze the live frames
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy){

        // Current time
        val currentTime = System.currentTimeMillis()

        // Only analyse every 1.5 second.
        if (currentTime - lastAnalyzedTime < 1500) {
            imageProxy.close()
            return
        }

        // Storing last analysis time
        lastAnalyzedTime = currentTime


        // Access the current camera frame
        val currentImage = imageProxy.image

        if (currentImage != null) {

            // Wrapping the cameraX frame into a ML Kit image format
            // (cannot use the frame directly
            val image = InputImage.fromMediaImage(
                currentImage,
                imageProxy.imageInfo.rotationDegrees
            )

            // Analyzing the picture with the model
            imageLabeler.process(image)

                // Iterating over the labels that has been detected and logging the details
                .addOnSuccessListener {labels ->

                    // Show only the best label (measured by confidence)
                    val bestLabel = labels.maxByOrNull {it.confidence}

                    bestLabel?.let { label ->
                        Log.d(
                            "ML",
                            "Detected: ${label.text} " +
                                    "Confidence: ${label.confidence}"
                        )

                        // Returning the best label
                        onLabelDetected(label.text)
                    }

                    /* for (label in labels){
                        Log.d("ML", "Detected: ${label.text} " + "Confidence: ${label.confidence}"
                        )

                        // Returning the detected label as a callback
                        onLabelDetected(label.text)
                    } */
                }

                .addOnFailureListener { e ->

                    Log.e("ML", "No label found that matches the image", e)
                }

                .addOnCompleteListener {
                    // Closing the frame buffer / scanner
                    imageProxy.close()
                }
        } else {

            // close also if frame is null
            imageProxy.close()
        }
    }
}