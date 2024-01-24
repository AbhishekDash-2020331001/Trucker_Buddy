package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.TripDetailScreen
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripDetailScreenActivity : ComponentActivity(),TripDetailScreenCallBack {
    val tripDetailScreenCallBack=this
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var tripId: String
    private lateinit var receivedIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth
        db=Firebase.firestore
        receivedIntent = intent
        tripId = (receivedIntent.getSerializableExtra("tripId") as? String)!!
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting8("")
                    TripDetailScreen(tripId=tripId, tripDetailScreenCallBack = tripDetailScreenCallBack)
                }
            }
        }
    }

    override fun placeBid(tripId: String, tripCreatorId: String, bidAmount: String) {
        val currentUser=auth.currentUser
        val uid= currentUser?.uid
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val bid= hashMapOf(
            "Trip Id" to tripId,
            "Bidder Id" to uid,
            "Creator Id" to tripCreatorId,
            "Bid Amount" to bidAmount
        )
        val bidId="$tripId-$uid-$tripCreatorId-$currentDate"
        db.collection("Bids")
            .document(bidId)
            .set(bid, SetOptions.merge())
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
        uid?.let { db.collection("Clients").document(it) }
            ?.update("My Bids", FieldValue.arrayUnion(bidId))
            ?.addOnSuccessListener {
                // Handle success
                println("Element added to the array field.")
            }
            ?.addOnFailureListener { e ->
                // Handle failure
                println("Error adding element to the array field: $e")
            }
        tripId.let { db.collection("Trips").document(it) }
            .update("Bids", FieldValue.arrayUnion(bidId))
            .addOnSuccessListener {
                // Handle success
                println("Element added to the array field.")
                Toast.makeText(
                    baseContext,
                    "Your bid has been placed",
                    Toast.LENGTH_SHORT,
                ).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Handle failure
                println("Error adding element to the array field: $e")
            }

    }
}

@Composable
fun Greeting8(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview8() {
    TruckerBuddyTheme {
        Greeting8("Android")
    }
}