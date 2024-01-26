package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.MyRunningTrips
import com.abhishek.truckerbuddy.composables.TripBrief
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyRunningTripsActivity : ComponentActivity(),MyRunningTripsCallBack {

    val myRunningTripsCallBack=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val auth= Firebase.auth
                    val db= Firebase.firestore
                    var runningTripsList by remember { mutableStateOf(emptyList<String>()) }
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val uid = currentUser.uid
                        val clientCollectionRef = db.collection("Clients")
                        val userDocumentRef = clientCollectionRef.document(uid)

                        userDocumentRef.get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    runningTripsList = documentSnapshot.get("Running Trips") as? List<String> ?: emptyList()
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
                    var trips  by remember { mutableStateOf <List<TripBrief>>(emptyList())}
                    if (currentUser != null) {
                        db.collection("Trips")
                            .whereEqualTo("Post Creator", currentUser.uid)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val pickUpDivision=document.getString("Pick Up Division")?:""
                                    val pickUpZilla=document.getString("Pick Up Zilla")?:""
                                    val deliveryDivision=document.getString("Delivery Division")?:""
                                    val deliveryZilla=document.getString("Delivery Zilla")?:""
                                    val truckMap = document.get("Needed Truck") as? Map<String, Any>
                                    var name:String?= null
                                    var highestCapacity:Int?= null
                                    if (truckMap != null) {
                                        // Retrieve values associated with keys
                                        name = truckMap["name"] as? String
                                        highestCapacity = truckMap["highestCapacity"] as? Int

                                        if (name != null && highestCapacity != null) {
                                            // Use the retrieved values
                                            println("Name: $name")
                                            println("Highest Capacity: $highestCapacity")
                                        } else {
                                            // Handle the case where values are null or not of the expected type
                                            println("Values are null or not of the expected type.")
                                        }
                                    } else {
                                        // Handle the case where "Truck" is not a map or is null
                                        println("Truck field is not a map or is null.")
                                    }
                                    trips+= TripBrief(
                                        pickUpTime = document.getString("Pick Up Time")?:"",
                                        pickUpDate = document.getString("Pick Up Date")?:"",
                                        pickUpLocation = "$pickUpDivision, $pickUpZilla",
                                        deliveryLocation = "$deliveryDivision, $deliveryZilla",
                                        truckType = name?:"",
                                        goodsType = document.getString("Type of Good")?:"",
                                        truckCapacity = highestCapacity?:0,
                                        tripId = document.id
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error getting documents: ", exception)
                            }
                    }
                    MyRunningTrips(myRunningTripsCallBack = myRunningTripsCallBack, trips = trips)
                }
            }
        }
    }

    override fun viewResponses(tripId: String) {
        Log.d(TAG,tripId)
        val intent= Intent(this@MyRunningTripsActivity,ViewResponsesScreenActivity::class.java)
        intent.putExtra("tripId",tripId)
        startActivity(intent)
    }
}

@Composable
fun Greeting6(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview6() {
    TruckerBuddyTheme {
        Greeting6("Android")
    }
}