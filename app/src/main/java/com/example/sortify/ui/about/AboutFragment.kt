package com.example.sortify.ui.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.sortify.R
import com.example.sortify.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {

    private var _b: FragmentAboutBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentAboutBinding.bind(view)

        // If your About screen is static text only, you don’t need to do anything here.
        // Put any future click listeners here if you add buttons/links.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
