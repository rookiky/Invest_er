package com.example.invest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.invest.ui.theme.InvestTheme
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        setContent {
            InvestTheme {
                LoginScreen ({ email, password ->
                    signInUser(email, password)
                },
                    onRegisterClick = { startActivity(Intent(this, RegistrationActivity::class.java)) }
                )
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sign in error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginClick: (String, String) -> Unit, onRegisterClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Black background from theme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(

                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onLoginClick("email", "password") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,

            ),
            ) {
                Text("Login", color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Don't have an account? Sign Up", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}