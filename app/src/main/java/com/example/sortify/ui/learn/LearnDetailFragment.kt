package com.example.sortify.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sortify.R
import com.example.sortify.databinding.FragmentLearnDetailBinding
import com.example.sortify.model.WasteCatalog
import com.example.sortify.ui.util.Haptics
import com.google.android.material.button.MaterialButton

class LearnDetailFragment : Fragment() {

    private var _b: FragmentLearnDetailBinding? = null
    private val b get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentLearnDetailBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val classId = requireArguments().getInt("classId", -1)
        val info = WasteCatalog.info(classId)

        val name = info?.name ?: WasteCatalog.safeName(classId)
        val recyclable = info?.recyclable ?: WasteCatalog.safeRecyclable(classId)

        // These are usually present in your layout, but we still fetch safely
        findText(view, "tvTitle")?.text = name
        
        // Find existing imageview by ID or add finding logic if using binding
        val ivIcon = view.findViewById<android.widget.ImageView>(R.id.ivItemIcon)
        if (info != null) {
            ivIcon?.setImageResource(info.getDisplayImage())
        } else {
             // Fallback if no info found (shouldn't happen for valid IDs)
             ivIcon?.setImageResource(R.drawable.ic_item_placeholder)
        }

        findText(view, "tvStatus")?.apply {
            text = if (recyclable) "Recyclable" else "Non-Recyclable"
            setTextColor(
                if (recyclable)
                    colorByName("recyclable", android.R.color.holo_green_dark)
                else
                    colorByName("non_recyclable", android.R.color.holo_red_dark)
            )
        }

        findText(view, "tvWhat")?.text = info?.whatItIs ?: "No details available."
        findText(view, "tvWhy")?.text = info?.why ?: "No details available."
        findText(view, "tvHow")?.text = info?.howToDispose ?: "Follow local disposal rules."

        // Support both possible IDs (your XML might use tvDo OR tvDos)
        val dosText = bulletLines(info?.doTips ?: listOf("Follow local guidance."))
        findText(view, "tvDo")?.text = dosText
        findText(view, "tvDos")?.text = dosText

        val dontsText = bulletLines(info?.dontTips ?: listOf("Avoid contaminating recycling."))
        findText(view, "tvDont")?.text = dontsText
        findText(view, "tvDonts")?.text = dontsText

        findText(view, "tvConfusions")?.text = info?.commonConfusions ?: "Rules may vary by location."

        // Support different possible button IDs
        val btn =
            findButton(view, "btnTryScan")
                ?: findButton(view, "btnViewLearn")
                ?: findButton(view, "btnBackToScan")

        btn?.setOnClickListener { v ->
            Haptics.medium(v)
            // change destination if your nav graph uses a different action/destination id
            findNavController().navigate(R.id.scanEntryFragment)
        }

        // Back button
        val backBtn = view.findViewById<android.widget.ImageButton>(R.id.btnBack)
        backBtn?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bulletLines(lines: List<String>): String =
        lines.joinToString(separator = "\n") { "• $it" }

    private fun findText(root: View, idName: String): TextView? {
        val id = resources.getIdentifier(idName, "id", requireContext().packageName)
        if (id == 0) return null
        return root.findViewById(id)
    }

    private fun findButton(root: View, idName: String): MaterialButton? {
        val id = resources.getIdentifier(idName, "id", requireContext().packageName)
        if (id == 0) return null
        return root.findViewById(id)
    }

    private fun colorByName(colorName: String, fallback: Int): Int {
        val id = resources.getIdentifier(colorName, "color", requireContext().packageName)
        return if (id != 0) ContextCompat.getColor(requireContext(), id)
        else ContextCompat.getColor(requireContext(), fallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
