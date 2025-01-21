package com.example.invest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.invest.ui.theme.InvestTheme
//import com.squareup.picasso.Picasso
import android.widget.ImageView
import androidx.compose.ui.Alignment

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get user data from the intent
        val name = intent.getStringExtra("name") ?: "No Name"
        val gender = intent.getStringExtra("gender") ?: "Not Provided"
        var profileType = intent.getStringExtra("profileType") ?: "Not Provided"
        val profileImageUrl = intent.getStringExtra("profileImageUrl")

        setContent {
            InvestTheme {
                ProfileScreen(name = name, gender = gender, profileType = profileType, profileImageUrl = profileImageUrl)
            }
        }
    }
}

@Composable
fun ProfileScreen(name: String, gender: String, profileType: String, profileImageUrl: String?) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Profile Image if URL exists


            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Name: $name", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Gender: $gender", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Profile Type: $profileType", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
