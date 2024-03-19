package com.abhishek.truckerbuddy

import android.content.ContentValues
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
import androidx.compose.ui.Modifier
import com.abhishek.truckerbuddy.composables.PostScreen
import com.abhishek.truckerbuddy.composables.Truck
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class PostActivity : ComponentActivity(),PostCallBack {
    private val db = Firebase.firestore
    private lateinit var receivedIntent: Intent
    private lateinit var auth: FirebaseAuth
    private lateinit var receivedTruck: Truck
    private lateinit var ptime: LocalTime
    private lateinit var pdate: LocalDate
    val postCallBack=this
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        receivedIntent = intent
        receivedTruck = (receivedIntent.getSerializableExtra("selectedTruck") as? Truck)!!
        ptime = (receivedIntent.getSerializableExtra("ptime") as? LocalTime)!!
        pdate = (receivedIntent.getSerializableExtra("pdate") as? LocalDate)!!
        setContent {
            TruckerBuddyTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Log.d(TAG,"Checking")
                    PostScreen(postCallBack = postCallBack,truck=receivedTruck, ptime=ptime, pdate=pdate)

                }
            }
        }
    }

    override fun gotoFeed() {
        val intent= Intent(this@PostActivity,FeedActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun gotoTruckScreen(ptime: LocalTime, pdate: LocalDate) {
        val intent= Intent(this@PostActivity,TruckScreenActivity::class.java)
        intent.putExtra("ptime",ptime)
        intent.putExtra("pdate",pdate)
        startActivity(intent)
    }



    override fun createPost(
        pickUpDate: String,
        pickUpTime: String,
        pickUpDivision: String,
        pickUpZilla: String,
        deliveryDivision: String,
        deliveryZilla: String,
        typeOfGood: String
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if(!currentUser.isEmailVerified){
                Toast.makeText(
                    baseContext,
                    "Please verify your mail first!",
                    Toast.LENGTH_SHORT,
                ).show()
                return
            }

        }
        val uid = currentUser?.uid
        val docId = uid?.let { generateDocumentId(it) }
        uid?.let { userId ->
            db.collection("Clients").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("Name")
                        userName?.let { name ->


                            val trip = hashMapOf(
                                "Rated" to false,
                                "Assigned" to "null",
                                "Ongoing" to false,
                                "Post Creator Name" to name,
                                "Post Creator" to uid,
                                "Trip" to docId,
                                "Running" to true,
                                "Pick Up Date" to pickUpDate,
                                "Pick Up Time" to pickUpTime,
                                "Needed Truck" to receivedTruck,
                                "Pick Up Division" to pickUpDivision,
                                "Pick Up Zilla" to pickUpZilla,
                                "Delivery Division" to deliveryDivision,
                                "Delivery Zilla" to deliveryZilla,
                                "Type of Good" to typeOfGood
                            )
                            if (docId != null) {
                                db.collection("Trips").document(docId)
                                    .set(trip, SetOptions.merge())
                                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                            }
                            uid.let { db.collection("Clients").document(it) }
                                ?.update("Running Trips", FieldValue.arrayUnion(docId))
                                ?.addOnSuccessListener {

                                    println("Element added to the array field.")
                                    Toast.makeText(
                                        baseContext,
                                        "Post Added Successfully",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    val intent=Intent(this@PostActivity,MyRunningTripsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                ?.addOnFailureListener { e ->

                                    println("Error adding element to the array field: $e")
                                }
                        }
                    } else {
                        Log.e(ContentValues.TAG, "User document does not exist.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error getting user document", e)
                }
        }

    }

    override fun gotoProfileScreen() {
        val intent= Intent(this@PostActivity,ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}

fun generateDocumentId(uid: String): String {

    val currentDateTime = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    val formattedDateTime = dateFormat.format(currentDateTime)
    return "$uid-$formattedDateTime"
}



