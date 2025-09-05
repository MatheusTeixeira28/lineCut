package com.br.linecut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.br.linecut.data.firebase.FirebaseConfig
import com.br.linecut.ui.navigation.LineCutNavigation
import com.br.linecut.ui.navigation.Screen
import com.br.linecut.ui.theme.LineCutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Firebase
        FirebaseConfig.initialize(this)
        
        enableEdgeToEdge()
        setContent {
            LineCutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LineCutNavigation(
                        modifier = Modifier.padding(innerPadding),
                        startDestination = Screen.LOGIN
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LineCutTheme {
        LineCutNavigation()
    }
}