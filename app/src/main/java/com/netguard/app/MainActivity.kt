package com.netguard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.netguard.app.presentation.navigation.NetGuardRoot
import com.netguard.app.presentation.theme.NetGuardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Dark is the product default. Theme selection (System/Light/Dark)
            // is persisted via DataStore in a later phase.
            NetGuardTheme(darkTheme = true) {
                NetGuardRoot()
            }
        }
    }
}
