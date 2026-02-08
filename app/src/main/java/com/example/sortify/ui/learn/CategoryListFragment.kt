package com.example.sortify.ui.learn

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sortify.R
import com.example.sortify.databinding.FragmentCategoryListBinding
import com.example.sortify.model.WasteCatalog
import com.example.sortify.model.WasteCategory
import com.example.sortify.ui.util.Haptics

class CategoryListFragment : Fragment(R.layout.fragment_category_list) {

    private var _b: FragmentCategoryListBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentCategoryListBinding.bind(view)

        val categoryArg = requireArguments().getString("category", "")
        val queryArg = requireArguments().getString("query", "").trim()

        val title = when {
            queryArg.isNotEmpty() -> "Search results"
            categoryArg.isNotEmpty() -> "Category: ${categoryArg.lowercase().replaceFirstChar { it.uppercase() }}"
            else -> "Browse"
        }
        b.tvTitle.text = title

        val adapter = CategoryListAdapter { info ->
            Haptics.light(b.rv)
            findNavController().navigate(
                R.id.learnDetailFragment,
                bundleOf("classId" to info.classId)
            )
        }

        b.rv.layoutManager = LinearLayoutManager(requireContext())
        b.rv.adapter = adapter

        val list = when {
            queryArg.isNotEmpty() -> WasteCatalog.search(queryArg)
            categoryArg.isNotEmpty() -> {
                val cat = runCatching { WasteCategory.valueOf(categoryArg) }.getOrNull()
                if (cat != null) WasteCatalog.byCategory(cat) else emptyList()
            }
            else -> WasteCatalog.all()
        }

        b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        adapter.submit(list)

        b.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
