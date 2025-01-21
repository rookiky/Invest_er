package com.example.invest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
                RegistrationScreen ({ email, password, name, sex, profileType ->
                    registerUser(email, password, name, sex, profileType)
                },
                onLoginClick = { startActivity(Intent(this, LoginActivity::class.java)) }
                )}
        }
    }

    private fun registerUser(email: String, password: String, name: String, sex: String, profileType: String) {
        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this, "Sign up error", Toast.LENGTH_SHORT).show()
            } else {
                val userId = mAuth?.currentUser!!.uid
                val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                val userInfo = mapOf(
                    "name" to name,
                    "sex" to sex,
                    "profileType" to profileType,
                    "profileImageUrl" to "default"
                )
                currentUserDb.updateChildren(userInfo)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(onRegisterClick: (String, String, String, String, String) -> Unit, onLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    val genders = listOf("Male", "Female", "Other")
    var selectedProfileType by remember { mutableStateOf("") }
    val profileTypes = listOf("Investor", "Founder")
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
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center, // Centers content in the Box
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(

                    text = "Welcome back!",
                    style = MaterialTheme.typography.titleLarge, // Use large predefined typography
                    color = MaterialTheme.colorScheme.tertiary // Adjust color for contrast
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
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

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
            )
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = gender)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Select User Type:", style = MaterialTheme.typography.titleMedium)
            profileTypes.forEach { profileType ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedProfileType == profileType,
                        onClick = { selectedProfileType = profileType }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = profileType)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && selectedGender.isNotEmpty()) {
                        onRegisterClick(email, password, name, selectedGender, selectedProfileType)
                    } else {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,

                    ),
            ) {
                Text("Register", color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Already have an account? Log In", color = MaterialTheme.colorScheme.onBackground)
            }

        }
    }


}

