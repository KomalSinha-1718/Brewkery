package com.brewkery.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.brewkery.app.ui.navigation.BrewkeryNavHost
import com.brewkery.app.ui.theme.BrewkeryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as BrewkeryApp).container
        setContent {
            BrewkeryTheme {
                BrewkeryNavHost(container)
            }
        }
    }
}
