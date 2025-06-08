package com.pnu.ailifelog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pnu.ailifelog.model.SignUp.TokenManager
import com.pnu.ailifelog.nav.NavGraph
import com.pnu.ailifelog.ui.theme.AilifelogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = TokenManager.getAccessToken(this)
        val startPage = if (token.isNullOrEmpty()) "IdPage" else "MainPage"
        enableEdgeToEdge()
        setContent {
            AilifelogTheme {
                NavGraph(startPage = startPage)
            }
        }
    }
}


