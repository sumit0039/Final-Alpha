package com.softwill.alpha.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.softwill.alpha.R

class DialogFragment : Fragment() {

    private lateinit var frameLayout: FrameLayout
    private lateinit var loader: ProgressBar // or any other loader view

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.progress_dialogue_layout, container, false)
        frameLayout = view.findViewById(R.id.frameLayout)
        loader = ProgressBar(requireContext()) // Create your loader view
        // Set loader properties if needed
        return view
    }

    // Call this function when you want to show the loader
    private fun showLoader() {
        LoaderUtil.showLoader(frameLayout, loader)
    }

    // Call this function when loading is done to hide the loader
    private fun hideLoader() {
        LoaderUtil.hideLoader(frameLayout, loader)
    }

    // Call showLoader() when you want to show loader, and hideLoader() when loading is done.
}
