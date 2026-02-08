package com.example.sortify.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sortify.R
import com.example.sortify.data.ScanWithItems
import com.example.sortify.databinding.FragmentHomeBinding
import com.example.sortify.ui.util.Haptics
import com.example.sortify.viewmodel.ScanViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!

    private val vm: ScanViewModel by activityViewModels()
    private lateinit var adapter: RecentScanAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentHomeBinding.bind(view)

        adapter = RecentScanAdapter { scanId ->
            vm.deleteScan(scanId)
        }

        b.rvRecent.layoutManager = LinearLayoutManager(requireContext())
        b.rvRecent.adapter = adapter

        b.btnStartScanning.setOnClickListener {
            Haptics.medium(it)
            findNavController().navigate(R.id.action_home_to_scanEntry)
        }

        b.btnLearn.setOnClickListener {
            Haptics.light(it)
            findNavController().navigate(R.id.action_home_to_learn)
        }

        vm.stats.observe(viewLifecycleOwner) { stats ->
            val hasHistory = stats.totalScans > 0
            b.cardStats.visibility = if (hasHistory) View.VISIBLE else View.GONE
            if (hasHistory) {
                b.tvStats.text =
                    "Total scans: ${stats.totalScans}\n" +
                            "Recyclable items found: ${stats.totalRecyclableFound}\n" +
                            "Scans today: ${stats.scansToday}"
            }
        }

        // IMPORTANT: Explicit type fixes the "infer T" error.
        vm.recentScans.observe(viewLifecycleOwner) { list: List<ScanWithItems>? ->
            b.tvEmptyHistory.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE

            // Use orEmpty() so Kotlin knows the type is List<ScanWithItems>
            adapter.submit(list.orEmpty())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
