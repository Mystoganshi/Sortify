package com.example.sortify.ui.scan

import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sortify.R
import com.example.sortify.databinding.FragmentLiveScanBinding
import com.example.sortify.model.WasteCatalog
import com.example.sortify.ui.DrawItem
import com.example.sortify.ui.util.Haptics
import com.example.sortify.viewmodel.LiveDetection
import com.example.sortify.viewmodel.ScanViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class LiveScanFragment : Fragment(R.layout.fragment_live_scan) {

    private var _b: FragmentLiveScanBinding? = null
    private val b get() = _b!!

    private val vm: ScanViewModel by activityViewModels()

    private var cameraExecutor: ExecutorService? = null
    private var detector: ObjectDetector? = null

    private var sheetBehavior: BottomSheetBehavior<View>? = null

    // Less clutter
    private val maxBoxesToDraw = 6
    private val minScoreToDraw = 0.55f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentLiveScanBinding.bind(view)

        if (cameraExecutor == null) {
            cameraExecutor = Executors.newSingleThreadExecutor()
        }

        setupLegend()
        setupBottomSheet()

        // Tap for info (NOT the whole card)
        b.tvTapForInfo.setOnClickListener {
            Haptics.light(it)
            findNavController().navigate(R.id.action_liveScan_to_detectedItems)
        }

        b.btnSaveScan.isEnabled = false
        b.btnSaveScan.alpha = 0.55f
        b.btnSaveScan.setOnClickListener {
            Haptics.success(it)
            vm.saveCurrentScan()
        }

        vm.savedEvent.observe(viewLifecycleOwner) {
            if (_b == null) return@observe
            showSavedBanner()
        }

        startCamera()
    }

    private fun setupLegend() {
        // Collapsible legend
        b.legendHeader.setOnClickListener {
            val expanded = b.legendContent.visibility == View.VISIBLE
            b.legendContent.visibility = if (expanded) View.GONE else View.VISIBLE
            b.ivLegendChevron.animate().rotation(if (expanded) 0f else 180f).setDuration(160).start()
        }
    }

    private fun setupBottomSheet() {
        // Make sure the bottom sheet is always visible + draggable
        sheetBehavior = BottomSheetBehavior.from(b.cardResult)

        sheetBehavior?.apply {
            isHideable = false

            // ✅ FIX for your error: DO NOT use "fitToContents" directly
            // Use the public API:
            setFitToContents(true)

            // Bigger peek height so it's clearly visible
            peekHeight = dp(140)

            // Force it into collapsed state after layout
            b.cardResult.post {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        // Tap handle to expand/collapse
        b.resultHandle.setOnClickListener {
            val behavior = sheetBehavior ?: return@setOnClickListener
            behavior.state =
                if (behavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).roundToInt()
    }

    private fun showSavedBanner() {
        val binding = _b ?: return
        binding.cardSaved.visibility = View.VISIBLE
        binding.cardSaved.alpha = 0f
        binding.cardSaved.animate()
            .alpha(1f)
            .setDuration(220)
            .withEndAction {
                binding.cardSaved.animate()
                    .alpha(0f)
                    .setDuration(350)
                    .setStartDelay(700)
                    .withEndAction { _b?.cardSaved?.visibility = View.GONE }
                    .start()
            }
            .start()
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(requireContext())
        providerFuture.addListener({
            val binding = _b ?: return@addListener
            val provider = providerFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val selector = CameraSelector.DEFAULT_BACK_CAMERA

            val localModel = LocalModel.Builder()
                .setAssetFilePath("tm_waste_v1.tflite")
                .build()

            val options = CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .build()

            detector?.close()
            detector = ObjectDetection.getClient(options)

            analysis.setAnalyzer(cameraExecutor!!) { imageProxy ->
                val media = imageProxy.image
                if (media == null) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val isFront = selector == CameraSelector.DEFAULT_FRONT_CAMERA

                _b?.overlay?.post {
                    _b?.overlay?.setSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        rotationDegrees,
                        isFront
                    )
                }

                val inputImage = InputImage.fromMediaImage(media, rotationDegrees)

                val localDetector = detector
                if (localDetector == null) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                localDetector.process(inputImage)
                    .addOnSuccessListener { objects ->
                        val bindingNow = _b ?: return@addOnSuccessListener

                        val knownLive = ArrayList<LiveDetection>()
                        val drawCandidates = ArrayList<Pair<DrawItem, Float>>()

                        var unknownCount = 0

                        for (o in objects) {
                            val best = o.labels.maxByOrNull { it.confidence }
                            val idx = best?.index ?: -1
                            val score = best?.confidence ?: 0f

                            val rect = RectF(o.boundingBox)

                            val known = idx in 0..29
                            if (!known) unknownCount++

                            val className = if (known) WasteCatalog.safeName(idx) else "Unknown"
                            val recyclable = if (known) WasteCatalog.safeRecyclable(idx) else false

                            val label = if (known) className else "Unknown"

                            val color = when {
                                !known -> Color.parseColor("#F9A825") // amber
                                recyclable -> Color.parseColor("#2E7D32") // green
                                else -> Color.parseColor("#C62828") // red
                            }

                            if (score >= minScoreToDraw) {
                                drawCandidates.add(DrawItem(rect, label, color) to score)
                            }

                            if (known) {
                                knownLive.add(
                                    LiveDetection(
                                        classId = idx,
                                        name = className,
                                        recyclable = recyclable,
                                        rect = rect,
                                        score = score
                                    )
                                )
                            }
                        }

                        val drawItems = drawCandidates
                            .sortedByDescending { it.second }
                            .take(maxBoxesToDraw)
                            .map { it.first }

                        bindingNow.overlay.post { _b?.overlay?.setResults(drawItems) }

                        vm.updateDetections(knownLive)
                        updatePanel(knownLive, unknownCount)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }

            provider.unbindAll()
            provider.bindToLifecycle(viewLifecycleOwner, selector, preview, analysis)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun updatePanel(known: List<LiveDetection>, unknownCount: Int) {
        val binding = _b ?: return

        if (known.isEmpty() && unknownCount == 0) {
            binding.tvTop.text = "No item detected"
            binding.tvCounts.text = "Recyclable: 0 | Non-Recyclable: 0 | Unknown: 0"
            binding.btnSaveScan.isEnabled = false
            binding.btnSaveScan.alpha = 0.55f
            return
        }

        val recyclableCount = known.count { it.recyclable }
        val nonCount = known.count { !it.recyclable }

        if (known.isEmpty() && unknownCount > 0) {
            binding.tvTop.text = "Unknown item detected"
            binding.tvCounts.text = "Recyclable: 0 | Non-Recyclable: 0 | Unknown: $unknownCount"
            binding.btnSaveScan.isEnabled = false
            binding.btnSaveScan.alpha = 0.55f
            return
        }

        val top = known.maxByOrNull { it.score }!!
        val status = if (top.recyclable) "Recyclable" else "Non-Recyclable"

        binding.tvTop.text = "${top.name} - $status"
        binding.tvCounts.text =
            "Recyclable: $recyclableCount | Non-Recyclable: $nonCount | Unknown: $unknownCount"

        binding.btnSaveScan.isEnabled = true
        binding.btnSaveScan.alpha = 1f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
        sheetBehavior = null
    }

    override fun onDestroy() {
        super.onDestroy()
        detector?.close()
        detector = null
        cameraExecutor?.shutdown()
        cameraExecutor = null
    }
}
