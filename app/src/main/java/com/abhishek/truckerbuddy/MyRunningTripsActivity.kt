package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import com.abhishek.truckerbuddy.composables.CustomLoadingIndicator
import com.abhishek.truckerbuddy.composables.MyRunningTrips
import com.abhishek.truckerbuddy.composables.NoTripAvailablePage
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyRunningTripsActivity : ComponentActivity(),MyRunningTripsCallBack {

    val myRunningTripsCallBack=this
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var fetched by remember {
                        mutableStateOf(false)
                    }
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

                                }
                            }
                            .addOnFailureListener { exception ->

                            }
                    } else {

                    }
                    var trips  by remember { mutableStateOf <List<TripBrief>>(emptyList())}
                    if (currentUser != null) {
                        db.collection("Trips")
                            .whereEqualTo("Post Creator", currentUser.uid)
                            .get()
                            .addOnSuccessListener { documents ->
                                trips= emptyList()
                                for (document in documents) {
                                    val assigned=document.getString("Assigned")?:""
                                    val ongoing=document.getBoolean("Ongoing")==true
                                    val pickUpDivision=document.getString("Pick Up Division")?:""
                                    val pickUpZilla=document.getString("Pick Up Zilla")?:""
                                    val deliveryDivision=document.getString("Delivery Division")?:""
                                    val deliveryZilla=document.getString("Delivery Zilla")?:""
                                    val truckMap = document.get("Needed Truck") as? Map<String, Any>
                                    val rated = document.getBoolean("Rated")?:false
                                    var name:String?= null
                                    val creator= document.getString("Posts Creator")?:""
                                    var highestCapacity:Int?= null
                                    if (truckMap != null) {

                                        name = truckMap["name"] as? String
                                        highestCapacity = truckMap["highestCapacity"] as? Int

                                        if (name != null && highestCapacity != null) {

                                            println("Name: $name")
                                            println("Highest Capacity: $highestCapacity")
                                        } else {

                                            println("Values are null or not of the expected type.")
                                        }
                                    } else {

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
                                        tripId = document.id,
                                        assigned = assigned,
                                        ongoing = ongoing,
                                        rated = rated,
                                        creator = creator
                                    )
                                }
                                fetched=true
                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error getting documents: ", exception)
                            }
                    }
                    if(!fetched){
                        CustomLoadingIndicator()
                    }
                    else {
                        if(trips.isEmpty()){
                            NoTripAvailablePage()
                        }
                        else {
                            MyRunningTrips(myRunningTripsCallBack = myRunningTripsCallBack, trips = trips)
                        }
                    }
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

