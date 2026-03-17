package com.example.AgentUsersApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.AgentUsersApp.feature.auth.presentation.AuthNavGraph
import com.example.AgentUsersApp.ui.theme.AgentUsersAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgentUsersAppTheme {
                AuthNavGraph()
            }
            val text: String? = null
            println(text!!.length)
        }
    }
}
