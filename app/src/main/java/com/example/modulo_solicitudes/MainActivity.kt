package com.example.modulo_solicitudes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.modulo_solicitudes.data.local.AppDatabase
import com.example.modulo_solicitudes.ui.navigation.AppNavigation
import com.example.modulo_solicitudes.ui.theme.Modulo_SolicitudesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(applicationContext)
        setContent {
            Modulo_SolicitudesTheme {
                AppNavigation(database = database)
            }
        }
    }
}

