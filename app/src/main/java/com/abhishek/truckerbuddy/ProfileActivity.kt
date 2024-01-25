package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.ProfileScreen
import com.abhishek.truckerbuddy.composables.Truck
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileActivity : ComponentActivity(),ProfileCallBack {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var profileCallBack=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TruckerBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var name by remember { mutableStateOf("Loading") }
                    var username by remember { mutableStateOf("Loading") }
                    var email by remember { mutableStateOf("Loading") }
                    var phone by remember { mutableStateOf("Loading") }
                    var completedTrips by remember { mutableIntStateOf(0) }
                    var runningTrips by remember { mutableIntStateOf(0) }
                    var rating by remember { mutableStateOf(0.0) }
                    var profilePictureUrl by remember { mutableStateOf<String?>("https://firebasestorage.googleapis.com/v0/b/trucker-buddy-f7323.appspot.com/o/pp.jpg?alt=media&token=c86d8dc3-0f3d-42cd-a920-202fb46a0aa9") }

                    auth = Firebase.auth
                    db = Firebase.firestore

                    LaunchedEffect(auth.currentUser) {
                        // Check if the user is signed in
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val uid = currentUser.uid

                            // Retrieve data from Firestore based on UID
                            val clientCollectionRef = db.collection("Clients")
                            val userDocumentRef = clientCollectionRef.document(uid)

                            userDocumentRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        // Document exists, extract and update data
                                        name = documentSnapshot.getString("Name")?:""
                                        username = documentSnapshot.getString("Username")?:""
                                        email = documentSnapshot.getString("Email")?:""
                                        phone = documentSnapshot.getString("Phone")?:""

                                        // Retrieve Completed Trips as a List<String>
                                        val completedTripsList = documentSnapshot.get("Completed Trips") as? List<String>
                                        completedTrips = completedTripsList?.size ?: 0

                                        // Retrieve Running Trips as a List<String>
                                        val runningTripsList = documentSnapshot.get("Running Trips") as? List<String>
                                        runningTrips = runningTripsList?.size ?: 0
                                        rating = documentSnapshot.getDouble("Rating") ?: 0.0
                                        profilePictureUrl = documentSnapshot.getString("Photo")
                                    } else {
                                        // Document does not exist, handle accordingly
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Handle failures in retrieving data
                                }
                        } else {
                            // User is not signed in, handle accordingly (e.g., redirect to login)
                        }
                    }

                    // Pass the retrieved profile picture URL to the ProfileScreen
                    auth.currentUser?.let {
                        ProfileScreen(
                            profilePictureUrl = profilePictureUrl,
                            name = name,
                            username = username,
                            email = email,
                            phoneNumber = phone,
                            completedTrips = completedTrips,
                            runningTrips = runningTrips,
                            userRating = rating,
                            emailVerified = it.isEmailVerified,
                            profileCallBack =profileCallBack
                        )
                    }
                }
            }
        }
    }
    override fun gotoFeed() {
        val intent= Intent(this@ProfileActivity,FeedActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun myRunningTrips() {
        val intent=Intent(this@ProfileActivity,MyRunningTripsActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun sendVerificationMail() {


        val user = auth.currentUser
        if (user != null) {
            Log.d(TAG,"boolean ${user.isEmailVerified}")
        }
        if (user != null) {
            if(user.isEmailVerified){
                Toast.makeText(
                    baseContext,
                    "Email is already verified",
                    Toast.LENGTH_SHORT,
                ).show()
                return
            }

                user.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Verification mail sent.")
                            Toast.makeText(
                                baseContext,
                                "Verification mail sent",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
        }
    }

    override fun signOut() {
        auth.signOut()
        val intent= Intent(this@ProfileActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun gotoPost() {
        val truck= Truck(
            name = "Select",
            imageRes = "https://firebasestorage.googleapis.com/v0/b/trucker-buddy-f7323.appspot.com/o/truck.jpg?alt=media&token=e1675e46-78f8-471b-8b3f-18218f5d6cee",
            highestCapacity = 0
        )
        val intent= Intent(this@ProfileActivity,PostActivity::class.java)
        intent.putExtra("selectedTruck",truck)
        startActivity(intent)
        finish()
    }

    override fun gotoSettings() {
        TODO("Not yet implemented")
    }


}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    TruckerBuddyTheme {
        Greeting3("Android")
    }
}
