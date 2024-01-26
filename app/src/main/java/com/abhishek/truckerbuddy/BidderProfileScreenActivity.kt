package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhishek.truckerbuddy.composables.BidderInfo
import com.abhishek.truckerbuddy.composables.BidderProfileScreen
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BidderProfileScreenActivity : ComponentActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var db:FirebaseFirestore
    private lateinit var receivedIntent: Intent
    private lateinit var bidderId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receivedIntent = intent
        bidderId = (receivedIntent.getSerializableExtra("bidderId") as? String)?:""
        auth=Firebase.auth
        db=Firebase.firestore
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var name by remember { mutableStateOf("Loading") }
                    var email by remember { mutableStateOf("Loading") }
                    var phone by remember { mutableStateOf("Loading") }
                    var completedTrips by remember { mutableIntStateOf(0) }
                    var rating by remember { mutableStateOf(0.0) }
                    var profilePictureUrl by remember { mutableStateOf<String?>("https://firebasestorage.googleapis.com/v0/b/trucker-buddy-f7323.appspot.com/o/pp.jpg?alt=media&token=c86d8dc3-0f3d-42cd-a920-202fb46a0aa9") }

                    LaunchedEffect(bidderId) {
                            db.collection("Clients")
                                .document(bidderId)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        // Document exists, extract and update data
                                        name = documentSnapshot.getString("Name")?:""
                                        email = documentSnapshot.getString("Email")?:""
                                        phone = documentSnapshot.getString("Phone")?:""

                                        // Retrieve Completed Trips as a List<String>
                                        val completedTripsList = documentSnapshot.get("Completed Trips") as? List<String>
                                        completedTrips = completedTripsList?.size ?: 0

                                        // Retrieve Running Trips as a List<String
                                        rating = documentSnapshot.getDouble("Rating") ?: 0.0
                                        profilePictureUrl = documentSnapshot.getString("Photo")?:""
                                    } else {
                                        // Document does not exist, handle accordingly
                                    }

                                }
                                .addOnFailureListener { exception ->
                                    // Handle failures in retrieving data
                                }

                    }

                    if (profilePictureUrl == "https://firebasestorage.googleapis.com/v0/b/trucker-buddy-f7323.appspot.com/o/pp.jpg?alt=media&token=c86d8dc3-0f3d-42cd-a920-202fb46a0aa9") {
                        // Display loading indicator or handle loading state
                        // For example, you can show a loading spinner
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .padding(8.dp)
                            )
                        }
                    } else {
                        // Data has been loaded, call ProfileScreen
                        val data= profilePictureUrl?.let { BidderInfo(name = name, completedTrips = completedTrips, email = email, phone = phone, photo = it, rating = rating) }
                        if (data != null) {
                            BidderProfileScreen(
                                bidderInfo = data
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting10(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview10() {
    TruckerBuddyTheme {
        Greeting10("Android")
    }
}