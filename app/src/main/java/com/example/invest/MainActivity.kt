package com.example.invest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.invest.ui.theme.InvestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InvestTheme {
                MainScreen(
                    onLoginClick = { startActivity(Intent(this, LoginActivity::class.java)) },
                    onRegisterClick = { startActivity(Intent(this, RegistrationActivity::class.java)) }
                )
            }
        }
    }
}

@Composable
fun MainScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
            Text("Go to Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
            Text("Go to Registration")
        }
    }
}
