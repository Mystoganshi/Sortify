package com.example.sortify.detector

import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class MlKitTmDetector(
    private val modelAssetName: String,
    private val confidenceThreshold: Float = 0.5f
) : Detector {

    private val detector by lazy {
        val localModel = LocalModel.Builder()
            .setAssetFilePath(modelAssetName)
            .build()

        val options = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .setClassificationConfidenceThreshold(confidenceThreshold)
            .build()

        ObjectDetection.getClient(options)
    }

    override fun detect(
        imageProxy: ImageProxy,
        rotationDegrees: Int,
        onResult: (List<Detection>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            onError(IllegalStateException("ImageProxy.image is null"))
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        detector.process(inputImage)
            .addOnSuccessListener { objects ->
                val out = ArrayList<Detection>(objects.size)

                for (o in objects) {
                    val best = o.labels.maxByOrNull { it.confidence }
                    val idx = best?.index ?: -1
                    val score = best?.confidence ?: 0f

                    out.add(
                        Detection(
                            box = RectF(o.boundingBox),
                            classId = idx,
                            score = score
                        )
                    )
                }

                onResult(out)
            }
            .addOnFailureListener { e ->
                Log.e("Sortify", "Detector error", e)
                onError(e)
            }
    }

    override fun close() {
        try {
            detector.close()
        } catch (_: Throwable) {
        }
    }
}
