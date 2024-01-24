package com.abhishek.truckerbuddy.composables

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhishek.truckerbuddy.MyRunningTripsCallBack
import com.abhishek.truckerbuddy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/*data class Trip(
    val tripId: String,
    val tripCreatorName: String,
    val pickUpLocation: String,
    val deliveryLocation: String,
    val typeOfGood: String,
    val numberOfResponses: Int,
    val bestResponse: String
)*/

@Composable
fun MyRunningTrips(myRunningTripsCallBack: MyRunningTripsCallBack){
    val auth= Firebase.auth
    val db=Firebase.firestore
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
                    trips+=TripBrief(
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
    LazyColumn {
        items(trips) {
            TripCard(trip = it, str = "View Responses", onPlaceBidClick = {myRunningTripsCallBack.viewResponses(it.tripId)})

        }
    }

}

/*
@Composable
fun TripCard(
    tripId: String,
    tripCreatorName: String,
    pickUpLocation: String,
    deliveryLocation: String,
    typeOfGood: String,
    numberOfResponses: Int,
    bestResponse: String,
    onResponsesClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Trip ID and Creator Name
            Text(
                text = "Trip ID: $tripId",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Created by: $tripCreatorName",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pick-up Location
            ItemWithIcon(icon = Icons.Default.LocationOn, text = pickUpLocation)

            // Delivery Location
            ItemWithIcon(icon = Icons.Default.LocationOn, text = deliveryLocation)

            Spacer(modifier = Modifier.height(8.dp))

            // Type of Good
            ItemWithIcon(icon = Icons.Default.Category, text = typeOfGood)

            Spacer(modifier = Modifier.height(8.dp))

            // Responses Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemWithIcon(icon = Icons.Default.Chat, text = "Responses: $numberOfResponses")

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = onResponsesClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "View Responses")
                }
            }
        }
    }
}

@Composable
private fun ItemWithIcon(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview
@Composable
fun check(){
    TripCard(
        tripId = "123456",
        tripCreatorName = "John Doe",
        pickUpLocation = "City A",
        deliveryLocation = "City B",
        typeOfGood = "Electronics",
        numberOfResponses = 5,
        bestResponse = "50 tk",
        onResponsesClick = {
            // Handle responses button click
        }
    )

}
*/

