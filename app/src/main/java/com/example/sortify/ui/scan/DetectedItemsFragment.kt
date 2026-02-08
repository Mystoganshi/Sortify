package com.example.sortify.ui.scan

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sortify.R
import com.example.sortify.databinding.FragmentDetectedItemsBinding
import com.example.sortify.ui.util.Haptics
import com.example.sortify.ui.util.collectIn
import com.example.sortify.viewmodel.ScanViewModel

class DetectedItemsFragment : Fragment(R.layout.fragment_detected_items) {

    private var _b: FragmentDetectedItemsBinding? = null
    private val b get() = _b!!

    private val vm: ScanViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentDetectedItemsBinding.bind(view)

        val adapter = DetectedItemsAdapter { classId ->
            Haptics.light(b.rvDetected)
            findNavController().navigate(
                R.id.itemInfoFragment,
                bundleOf("classId" to classId)
            )
        }

        b.rvDetected.layoutManager = LinearLayoutManager(requireContext())
        b.rvDetected.adapter = adapter

        vm.latestDetections.collectIn(viewLifecycleOwner) { list ->
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            adapter.submit(list)
        }

        b.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
