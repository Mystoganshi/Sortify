package com.example.sortify.ui.learn

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sortify.R
import com.example.sortify.databinding.FragmentLearnBinding
import com.example.sortify.ui.util.Haptics

class LearnFragment : Fragment(R.layout.fragment_learn) {

    private var _b: FragmentLearnBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentLearnBinding.bind(view)

        fun openCategory(category: String) {
            Haptics.light(b.root)
            findNavController().navigate(
                R.id.categoryListFragment,
                bundleOf("category" to category, "query" to "")
            )
        }

        b.btnSearch.setOnClickListener {
            Haptics.medium(it)
            val q = b.etSearch.text?.toString()?.trim().orEmpty()
            findNavController().navigate(
                R.id.categoryListFragment,
                bundleOf("category" to "", "query" to q)
            )
        }

        // Optional “guides” (uses category list with prefilled queries)
        b.btnBasics.setOnClickListener {
            Haptics.light(it)
            findNavController().navigate(
                R.id.categoryListFragment,
                bundleOf("category" to "PAPER", "query" to "")
            )
        }

        b.btnHazard.setOnClickListener {
            Haptics.light(it)
            // Show e-waste + non-recyclable commonly hazardous via search keyword
            findNavController().navigate(
                R.id.categoryListFragment,
                bundleOf("category" to "EWASTE", "query" to "")
            )
        }

        b.cardPaper.setOnClickListener { openCategory("PAPER") }
        b.cardPlastic.setOnClickListener { openCategory("PLASTIC") }
        b.cardMetal.setOnClickListener { openCategory("METAL") }
        b.cardGlass.setOnClickListener { openCategory("GLASS") }
        b.cardOrganic.setOnClickListener { openCategory("ORGANIC") }
        b.cardEwaste.setOnClickListener { openCategory("EWASTE") }
        b.cardOther.setOnClickListener { openCategory("OTHER") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
