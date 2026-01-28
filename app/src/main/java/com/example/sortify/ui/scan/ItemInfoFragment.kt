package com.example.sortify.ui.scan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sortify.R
import com.example.sortify.databinding.FragmentItemInfoBinding
import com.example.sortify.model.WasteCatalog

class ItemInfoFragment : Fragment(R.layout.fragment_item_info) {

    private var _b: FragmentItemInfoBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentItemInfoBinding.bind(view)

        val classId = requireArguments().getInt("classId", -1)
        val info = WasteCatalog.info(classId)

        val name = info?.name ?: WasteCatalog.safeName(classId)
        val recyclable = info?.recyclable ?: WasteCatalog.safeRecyclable(classId)

        b.tvTitle.text = name
        b.tvStatus.text = if (recyclable) "Recyclable" else "Non-Recyclable"

        b.tvWhat.text = info?.whatItIs ?: "No details available."
        b.tvWhy.text = info?.why ?: "No details available."
        b.tvHow.text = info?.howToDispose ?: "Follow local disposal rules."
        b.tvDos.text = (info?.doTips ?: listOf("Follow local guidance.")).joinToString("\n") { "• $it" }
        b.tvDonts.text = (info?.dontTips ?: listOf("Avoid contaminating recycling.")).joinToString("\n") { "• $it" }
        b.tvConfusions.text = info?.commonConfusions ?: "Rules may vary by location."

        b.btnViewLearn.setOnClickListener {
            val args = Bundle().apply { putInt("classId", classId) }
            findNavController().navigate(R.id.learnDetailFragment, args)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
