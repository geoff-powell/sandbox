package com.greenmist.android.sandbox

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        web_view.settings.builtInZoomControls = true
        web_view.settings.setSupportZoom(true)

        assets.list("")?.filter {
            it.endsWith(".svg")
        }?.let {
            val adapter = ArrayAdapter<String>(
                this,
                R.layout.item_spinner,
                it
            )

            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.add("SVG Native")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        return true
    }

    private fun loadSvg(item: String?) {
        web_view.loadUrl("file:///android_asset/$item")
    }
}