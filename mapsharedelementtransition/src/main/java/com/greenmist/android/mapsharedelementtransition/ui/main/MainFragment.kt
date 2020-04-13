package com.greenmist.android.mapsharedelementtransition.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.webkit.WebSettings
import androidx.core.app.SharedElementCallback
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.transition.*
import com.greenmist.android.mapsharedelementtransition.R
import com.greenmist.android.mapsharedelementtransition.databinding.MainFragmentBinding
import com.greenmist.android.mapsharedelementtransition.transition.BottomCardTransition
import com.greenmist.android.mapsharedelementtransition.transition.CustomSlideTransition
import com.greenmist.android.mapsharedelementtransition.transition.SimpleTransition
import com.greenmist.android.mapsharedelementtransition.ui.details.DetailsFragment
import java.io.File
import java.io.FileOutputStream


class MainFragment : Fragment() {

    private var binding: MainFragmentBinding? = null
    private var webViewState: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            val webSettings: WebSettings = webView.settings
            webSettings.javaScriptEnabled = true

            if (webViewState != null) {
                webView.restoreState(webViewState)
            } else {
                webView.loadUrl("file:///android_asset/2d412792-60fa-4a2e-b1e9-237ae939491b.svg")
            }
            navigate.setOnClickListener {
                navigateToDetails()
            }
        }
    }

    private fun navigateToDetails() {
        val detailsFragment = DetailsFragment()

        val transaction = parentFragmentManager.beginTransaction()
            .replace(R.id.container, detailsFragment)
            .addToBackStack(null)

        setExitSharedElementCallback(object: SharedElementCallback() {
            override fun onRejectSharedElements(rejectedSharedElements: MutableList<View>?) {
                super.onRejectSharedElements(rejectedSharedElements)
            }

            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>,
                sharedElements: MutableList<View>,
                sharedElementSnapshots: MutableList<View>
            ) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots)
            }

            override fun onCaptureSharedElementSnapshot(
                sharedElement: View?,
                viewToGlobalMatrix: Matrix?,
                screenBounds: RectF?
            ): Parcelable {
                return super.onCaptureSharedElementSnapshot(
                    sharedElement,
                    viewToGlobalMatrix,
                    screenBounds
                )
            }

            override fun onSharedElementsArrived(
                sharedElementNames: MutableList<String>,
                sharedElements: MutableList<View>,
                listener: OnSharedElementsReadyListener
            ) {
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener)
            }

            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                binding?.run {
                    if (names.isNotEmpty() && names.size > sharedElements.size) {
                        if (names.contains(DetailsFragment.TRANSITION_NAME)) {
                            sharedElements[DetailsFragment.TRANSITION_NAME] = webView
                            webView.transitionName = DetailsFragment.TRANSITION_NAME
                        }
                        if (names.contains("test")) {
                            sharedElements["test"] = message
                            message.transitionName = "test"
                        }
                    }
                }
            }

            override fun onCreateSnapshotView(context: Context?, snapshot: Parcelable?): View {
                return super.onCreateSnapshotView(context, snapshot)
            }

            override fun onSharedElementStart(
                sharedElementNames: MutableList<String>,
                sharedElements: MutableList<View>,
                sharedElementSnapshots: MutableList<View>
            ) {
                super.onSharedElementStart(
                    sharedElementNames,
                    sharedElements,
                    sharedElementSnapshots
                )
            }
        })

        binding?.run {
            val file = File(requireContext().filesDir, "screen-${System.currentTimeMillis()}.png")
            val fileName = webView.saveViewBitmapToFile(file.path)

            detailsFragment.arguments = Bundle().apply {
                putString(DetailsFragment.KEY_FILE, fileName)
            }

            webView.transitionName = DetailsFragment.TRANSITION_NAME
            transaction.addSharedElement(webView, webView.transitionName)
        }

        val fadeDuration: Long = resources.getInteger(R.integer.transition_duration).toLong()
        val moveDuration: Long = resources.getInteger(R.integer.transition_duration).toLong()

        val delay = (moveDuration / 2.5).toLong()
        val exitTransitionFade = Fade()
        exitTransitionFade.duration = fadeDuration

        val exitTitleCollapsingTransition = CustomSlideTransition(Gravity.TOP)
        exitTitleCollapsingTransition.useViewAsBase = true
        exitTitleCollapsingTransition.addTarget(R.id.title)
        exitTitleCollapsingTransition.duration = fadeDuration - delay
        exitTitleCollapsingTransition.startDelay = delay

        val exitTransitionSet = TransitionSet()
        exitTransitionSet.addTransition(exitTransitionFade)
        exitTransitionSet.addTransition(exitTitleCollapsingTransition)

        this.exitTransition = exitTransitionSet

        val reenterTransitionFade = SimpleTransition()
        reenterTransitionFade.duration = fadeDuration

        val reenterTransitionSet = TransitionSet()
        reenterTransitionSet.addTransition(reenterTransitionFade)
        reenterTransitionSet.addTransition(exitTitleCollapsingTransition)

        this.reenterTransition = reenterTransitionSet

        val enterSharedElementTransitionSet = TransitionSet()
        enterSharedElementTransitionSet.addTransition(ChangeBounds())
        enterSharedElementTransitionSet.addTransition(ChangeClipBounds())
        enterSharedElementTransitionSet.addTransition(ChangeImageTransform())

        enterSharedElementTransitionSet.duration = moveDuration - delay
        enterSharedElementTransitionSet.startDelay = delay
        detailsFragment.sharedElementEnterTransition = enterSharedElementTransitionSet

        val returnSharedElementTransitionSet = TransitionSet()
        returnSharedElementTransitionSet.addTransition(ChangeBounds())
        returnSharedElementTransitionSet.addTransition(ChangeClipBounds())
        returnSharedElementTransitionSet.addTransition(ChangeImageTransform())
        enterSharedElementTransitionSet.duration = moveDuration - delay
        sharedElementReturnTransition = returnSharedElementTransitionSet

        val enterFade = Fade()
        enterFade.duration = fadeDuration
        enterFade.excludeChildren(R.id.appbar, true)

        val bottomCardTransition = CustomSlideTransition(Gravity.BOTTOM)
        bottomCardTransition.useViewAsBase = true
        bottomCardTransition.duration = moveDuration
        bottomCardTransition.addTarget(R.id.nested_scroll)

        val enterTransitionSet = TransitionSet()
        enterTransitionSet.addTransition(enterFade)
        enterTransitionSet.addTransition(bottomCardTransition)

        val returnTransitionSet = TransitionSet()
        returnTransitionSet.addTransition(Fade().apply { duration = fadeDuration })
        returnTransitionSet.addTransition(bottomCardTransition)

        detailsFragment.enterTransition = enterTransitionSet
        detailsFragment.returnTransition = returnTransitionSet

        transaction.commit()
    }

    private fun View.saveViewBitmapToFile(filename: String): String? {
        Log.d("Tag", "Start saving $filename")
        try {
            val bitmap = drawToBitmap(Bitmap.Config.ARGB_8888)
            FileOutputStream(filename).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it).apply {
                    bitmap.recycle()
                }
            }.run {
                Log.d("Tag", "Finished saving $filename")
                if (this) {
                    return filename
                }
            }
        } catch (e: Exception) {
            Log.e("Tag", "Error saving view to bitmap file", e)
        }
        Log.d("Tag", "Finished saving $filename")
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.webView?.let {
            webViewState = Bundle()
            it.saveState(webViewState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().filesDir.deleteRecursively()
    }
}