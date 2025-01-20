package com.example.invest

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.database.FirebaseDatabase

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

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signInUser(email: String, password: String) {
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address format", Toast.LENGTH_SHORT).show()
            Log.d("Login debug", email)
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {
                val userId = mAuth?.currentUser!!.uid
                val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

                currentUserDb.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").value.toString()
                        val gender = snapshot.child("sex").value.toString()
                        val profileType = snapshot.child("profileType").value.toString()
                        val profileImageUrl = snapshot.child("profileImageUrl").value.toString()

                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("name", name)
                            putExtra("gender", gender)
                            putExtra("profileType", profileType)
                            //putExtra("profileImageUrl", profileImageUrl) // If you have a profile image URL
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                }
            } else {
                val error = task.exception?.message ?: "Unknown error"
                Log.e("LoginDebug", "Login failed: $error")
                Toast.makeText(this, "Login failed: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginClick: (String, String) -> Unit, onRegisterClick: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Black background from theme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        )   {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                )
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center, // Centers content in the Box
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(

                    text = "Welcome",
                    style = MaterialTheme.typography.headlineLarge, // Use large predefined typography
                    color = MaterialTheme.colorScheme.tertiary // Adjust color for contrast
                )
            }

            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center, // Centers content in the Box
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(

                    text = "A Platform linking founders and investors",
                    style = MaterialTheme.typography.titleMedium, // Use large predefined typography
                    color = MaterialTheme.colorScheme.onBackground // Adjust color for contrast
                )
            }


            Spacer(modifier = Modifier.height(100.dp))
            TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
                    )

                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = { onLoginClick(email, password) },
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