package com.br.linecut

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.br.linecut.data.firebase.FirebaseConfig
import com.br.linecut.service.ServiceManager
import com.br.linecut.ui.navigation.LineCutNavigation
import com.br.linecut.ui.navigation.Screen
import com.br.linecut.ui.theme.LineCutTheme

class MainActivity : ComponentActivity() {
    
    // Launcher para solicitar permissão de notificações
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permissão respondida - não precisa fazer nada aqui
        // O service já será iniciado após o onCreate completar
        if (isGranted) {
            android.util.Log.d("MainActivity", "Permissão de notificações concedida")
        } else {
            android.util.Log.w("MainActivity", "Permissão de notificações negada")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Firebase
        FirebaseConfig.initialize(this)
        
        // Solicitar permissão de notificações (Android 13+)
        // O service será iniciado apenas após login bem-sucedido
        requestNotificationPermission()
        
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
    
    /**
     * Solicita permissão de notificações para Android 13+ (API 33+)
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permissão já concedida
                    android.util.Log.d("MainActivity", "Permissão de notificações já concedida")
                }
                else -> {
                    // Solicitar permissão
                    android.util.Log.d("MainActivity", "Solicitando permissão de notificações")
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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