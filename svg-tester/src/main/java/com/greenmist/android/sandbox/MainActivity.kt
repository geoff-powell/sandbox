package com.greenmist.android.sandbox

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import kotlinx.android.synthetic.main.activity_svg.*
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    private val css = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_svg)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.add("Web View")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, WebActivity::class.java))
        return true
    }

    override fun onResume() {
        super.onResume()
        assets.list("")?.filter {
            it.endsWith(".svg")
        }?.let {
            val adapter = ArrayAdapter<String>(
                this,
                R.layout.item_spinner,
                it
            )

            spinner.adapter = adapter
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loadSvg(adapter.getItem(position))
                }
            }
        }

        button.setOnClickListener {
            image_view.setOverrideCSS(edit_text.text.toString())
        }
    }

    private fun loadSvg(item: String?) {
        val svg = SVG.getFromAsset(assets, item)
        image_view.setSVGWithOverride(svg, css)
    }
}