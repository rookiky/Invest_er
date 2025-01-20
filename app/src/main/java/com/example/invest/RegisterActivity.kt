package com.example.invest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.invest.ui.theme.InvestTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : ComponentActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        setContent {
            InvestTheme {
                RegistrationScreen { email, password, name, sex ->
                    registerUser(email, password, name, sex)
                }
            }
        }
    }

    private fun registerUser(email: String, password: String, name: String, sex: String) {
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this, "Sign up error", Toast.LENGTH_SHORT).show()
            } else {
                val userId = mAuth?.currentUser!!.uid
                val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                val userInfo = mapOf(
                    "name" to name,
                    "sex" to sex,
                    "profileImageUrl" to "default"
                )
                currentUserDb.updateChildren(userInfo)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}

@Composable
fun RegistrationScreen(onRegisterClick: (String, String, String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    val genders = listOf("Male", "Female", "Other")

    // Get the current context for displaying the Toast
    val context = LocalContext.current

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
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Gender:", style = MaterialTheme.typography.titleMedium)
            genders.forEach { gender ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedGender == gender,
                        onClick = { selectedGender = gender }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = gender)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && selectedGender.isNotEmpty()) {
                        onRegisterClick(email, password, name, selectedGender)
                    } else {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }
    }


}

