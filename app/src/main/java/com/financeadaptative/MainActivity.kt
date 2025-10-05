package com.financeadaptative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.financeadaptative.ui.theme.FinPerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinPerTheme {
                FinPerApp()
            }
        }
    }
}