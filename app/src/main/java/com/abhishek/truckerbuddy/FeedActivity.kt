package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.CustomLoadingIndicator
import com.abhishek.truckerbuddy.composables.FeedScreen
import com.abhishek.truckerbuddy.composables.NoTripAvailablePage
import com.abhishek.truckerbuddy.composables.Truck
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime

class FeedActivity : ComponentActivity(),FeedCallBack {
    val feedCallBack=this
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val userId= Firebase.auth.currentUser?.uid
                    var fetched by remember {
                        mutableStateOf(false)
                    }
                    var trips by remember { mutableStateOf<List<TripBrief>>(emptyList()) }
                    val db = Firebase.firestore
                    db.collection("Trips")
                        .whereEqualTo("Running", true)
                        .get()
                        .addOnSuccessListener { documents ->
                            println("hahaha ${documents.size()}")
                            trips = emptyList()
                            for (document in documents) {
                                Log.d(ControlsProviderService.TAG, "${document.id} => ${document.data}")
                                val pickUpDivision = document.getString("Pick Up Division") ?: ""
                                val pickUpZilla = document.getString("Pick Up Zilla") ?: ""
                                val deliveryDivision = document.getString("Delivery Division") ?: ""
                                val deliveryZilla = document.getString("Delivery Zilla") ?: ""
                                val truckMap = document.get("Needed Truck") as? Map<String, Any>
                                val creator = document.getString("Post Creator")?:""
                                var name: String? = null
                                var highestCapacity: Int? = null
                                if (truckMap != null) {
                                    name = truckMap["name"] as? String
                                    highestCapacity = (truckMap["highestCapacity"] as? Long)?.toInt()

                                    if (name != null && highestCapacity != null) {

                                        println("Name: $name")
                                        println("Highest Capacity: $highestCapacity")
                                    } else {

                                        println("Values are null or not of the expected type.")
                                    }
                                } else {

                                    println("Truck field is not a map or is null.")
                                }
                                trips += TripBrief(
                                    pickUpTime = document.getString("Pick Up Time") ?: "",
                                    pickUpDate = document.getString("Pick Up Date") ?: "",
                                    pickUpLocation = "$pickUpDivision, $pickUpZilla",
                                    deliveryLocation = "$deliveryDivision, $deliveryZilla",
                                    truckType = name ?: "",
                                    goodsType = document.getString("Type of Good") ?: "",
                                    truckCapacity = highestCapacity ?: 0,
                                    tripId = document.id,
                                    assigned = document.getString("Assigned")?:"",
                                    ongoing = document.getBoolean("Ongoing")?:false,
                                    rated = document.getBoolean("Rated")?:false,
                                    creator = creator
                                )

                            }
                            fetched=true
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ControlsProviderService.TAG, "Error getting documents: ", exception)
                        }
                    if(!fetched){
                        CustomLoadingIndicator()
                    }
                    else{
                        if(trips.isEmpty()){
                            NoTripAvailablePage()
                        }
                        else{
                            FeedScreen(feedCallBack = feedCallBack, trips = trips)
                        }
                    }

                }
            }
        }
    }

    override fun placeYourBid(tripId:String,creator:String) {
        if((Firebase.auth.currentUser?.uid ?: "") == creator){
            Toast.makeText(
                baseContext,
                "You can't bid on your own trip",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        else{
            val intent= Intent(this@FeedActivity,TripDetailScreenActivity::class.java)
            intent.putExtra("tripId", tripId)
            startActivity(intent)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun gotoPostScreen() {
        val truck= Truck(
            name = "Select",
            imageRes = "https://firebasestorage.googleapis.com/v0/b/trucker-buddy-f7323.appspot.com/o/truck.jpg?alt=media&token=e1675e46-78f8-471b-8b3f-18218f5d6cee",
            highestCapacity = 0
        )
        val ptime= LocalTime.now()
        val pdate=LocalDate.now()
        val intent= Intent(this@FeedActivity,PostActivity::class.java)
        intent.putExtra("selectedTruck",truck)
        intent.putExtra("ptime",ptime)
        intent.putExtra("pdate",pdate)
        startActivity(intent)
        finish()
    }

    override fun gotoProfileScreen() {
        val intent= Intent(this@FeedActivity,ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}

