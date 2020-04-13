package com.greenmist.android.mapsharedelementtransition.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.greenmist.android.mapsharedelementtransition.R
import com.greenmist.android.mapsharedelementtransition.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment())
                .commitNow()
        }
    }
}