package com.greenmist.android.mapsharedelementtransition.ui.details

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import com.greenmist.android.mapsharedelementtransition.databinding.DetailsFragmentBinding
import com.greenmist.android.mapsharedelementtransition.databinding.DetailsFragmentTwoBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.util.concurrent.TimeUnit

class DetailsFragment : Fragment() {

    companion object {
        const val TRANSITION_NAME = "transition"
        const val KEY_FILE = "file"
    }

    private var binding: DetailsFragmentBinding? = null
    private val fileName by lazy {
        arguments?.getString(KEY_FILE)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (parentFragmentManager.backStackEntryCount > 0) {
                        parentFragmentManager.popBackStack()
                    } else {
                        isEnabled = false
                    }
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null && !fileName.isNullOrBlank()) {
            postponeEnterTransition(3000, TimeUnit.MILLISECONDS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fileName = this.fileName

        if (binding != null && !fileName.isNullOrBlank()) {
            binding?.test?.transitionName = TRANSITION_NAME

            binding?.root?.setOnTouchListener(View.OnTouchListener { _, _ ->
                true
            })

            Picasso.get().load(File(fileName))
                .noFade()
                .into(binding!!.test, object : Callback {
                    override fun onSuccess() {
                        binding?.test?.doOnPreDraw {
                            startPostponedEnterTransition()
                        }
                    }

                    override fun onError(e: Exception?) {
                        Log.e("Tag", "Error loading image", e)
                        startPostponedEnterTransition()
                    }
                })

            (enterTransition as? Transition)?.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionCancel(transition: Transition) {
                    super.onTransitionCancel(transition)
                    binding?.root?.setOnTouchListener(null)
                }

                override fun onTransitionEnd(transition: Transition) {
                    binding?.root?.setOnTouchListener(null)
                }
            })
        }
    }
}