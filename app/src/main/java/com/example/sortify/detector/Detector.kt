package com.example.sortify.detector

import android.graphics.RectF
import androidx.camera.core.ImageProxy

data class Detection(
    val box: RectF,
    val classId: Int,
    val score: Float
)

interface Detector {
    fun detect(
        imageProxy: ImageProxy,
        rotationDegrees: Int,
        onResult: (List<Detection>) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun close()
}
