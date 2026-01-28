package com.example.sortify.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sortify.R
import com.example.sortify.databinding.FragmentScanEntryBinding
import com.example.sortify.ui.util.Haptics

class ScanEntryFragment : Fragment(R.layout.fragment_scan_entry) {

    private var _b: FragmentScanEntryBinding? = null
    private val b get() = _b!!

    private val reqPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            findNavController().navigate(R.id.action_scanEntry_to_liveScan)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentScanEntryBinding.bind(view)

        b.btnOpenCamera.setOnClickListener {
            Haptics.medium(it)
            val ok = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (ok) {
                findNavController().navigate(R.id.action_scanEntry_to_liveScan)
            } else {
                reqPerm.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
