package com.abhishek.truckerbuddy.composables

import android.text.BoringLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarHalf
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.abhishek.truckerbuddy.R
import com.abhishek.truckerbuddy.TripBrief
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun MyRunningTripsCard(trip: TripBrief, modifier: Modifier = Modifier, onPlaceBidClick: () -> Unit = {}, str: String = "Place Your Bid") {
    var givenRating by remember {
        mutableStateOf(4.0)
    }
    var isHalfStar by remember {
        mutableStateOf((givenRating) % 1 != 0.0)
    }
    var isDialog by remember {
        mutableStateOf(false)
    }
    var strr by remember {
        mutableStateOf(str)
    }
    var assigned by remember {
        mutableStateOf("")
    }
    var statusText by remember {
        mutableStateOf("")
    }

    var truckImage by remember {
        mutableStateOf("")
    }
    var isTripDone by remember {
        mutableStateOf(false)
    }
    var color by remember {
        mutableStateOf(Color(0xFF008B8B))
    }
    if (str == "Closed") {
        toggleRunning(tripId = trip.tripId, isTripDone = true)
        isTripDone = true
        color = Color.Red
        statusText = "Marked as done"

    }
    // State to track whether the trip is marked as done or not
    //var flag=false
    val db = Firebase.firestore



    db.collection("Trips")
        .document(trip.tripId)
        .get() // Use SetOptions.merge() to merge the new values with existing ones
        .addOnSuccessListener {
            isTripDone = it.getBoolean("Running") == false
            assigned = it.getString("Assigned") ?: ""
        }
        .addOnFailureListener { e ->
            // Handle failure if needed
        }

    db.collection("Trucks")
        .document(trip.truckType)
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                truckImage = document.getString("Photo").toString()
            }
        }
        .addOnFailureListener { e -> /* Handle failure */ }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row for pick-up details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column for pick-up details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "Pick Up Date",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = MaterialTheme.typography.bodyMedium.fontWeight),
                        color = MaterialTheme.colorScheme.primary
                    )
                    BlinkingText(
                        text = trip.pickUpDate,
                        color = Color.Red,
                        size = 12.0f
                    )
                    Text(
                        text = "Pick Up Time",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = MaterialTheme.typography.bodyMedium.fontWeight),
                        color = MaterialTheme.colorScheme.primary
                    )
                    BlinkingText(
                        text = trip.pickUpTime,
                        color = Color.Red,
                        size = 12.0f
                    )
                    Text(
                        text = "Pick Up Location",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = MaterialTheme.typography.bodyMedium.fontWeight),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = trip.pickUpLocation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Delivery Location",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = MaterialTheme.typography.bodyMedium.fontWeight),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = trip.deliveryLocation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                // Column for tick mark button and truck image
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Tick mark button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                isTripDone = !isTripDone
                                statusText = if (isTripDone) "Marked as done" else "Mark as done"
                                color = if (isTripDone) Color.Red else Color(0xFF008B8B)
                                strr = if (isTripDone) "Closed" else "View Responses"
                                toggleRunning(tripId = trip.tripId, isTripDone = isTripDone)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            // Use different icons based on the state
                            if (isTripDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Marked as Done"
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = "Mark as Not Done"
                                )
                            }
                        }
                        // Display status text
                        Text(
                            text = statusText,
                            color = color,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }


                    // Load truck image using Coil
                    val painter = rememberImagePainter(data = truckImage, builder = {
                        crossfade(true)
                        placeholder(R.drawable.truckl)
                    })
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .padding(top = 8.dp) // Adjust the top padding to move the image up
                    )
                    Button(
                        onClick = {
                            println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                            isDialog = true;
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008B8B)),
                        contentPadding = PaddingValues(8.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = "Rate Driver", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Truck type and capacity
            Text(
                text = "Truck Type: ${trip.truckType}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = MaterialTheme.typography.bodyMedium.fontWeight),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Capacity: ${trip.truckCapacity}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = MaterialTheme.typography.bodyMedium.fontWeight),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Place your bid button with blinking animation
            BlinkingButton(text = strr, color = color, onClick = onPlaceBidClick)
        }
    }
    if (isDialog) {
        Dialog(onDismissRequest = {
            isDialog = false
            uploadRating(rating = givenRating, id = assigned)
        }) {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .wrapContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rate Your Driver",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        for (index in 1..5) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (index <= givenRating) Color(0xFF00FFFF) else Color.White)
                                    .clickable {
                                        givenRating = index.toDouble()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Star",
                                    tint = Color.Yellow,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            isDialog = false
                            uploadRating(rating = givenRating, id = assigned)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008B8B))
                    ) {
                        Text(
                            text = "Submit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


fun uploadRating(rating: Double, id:String){
    val db=Firebase.firestore
    db
        .collection("Clients")
        .document(id)
        .update("Score",FieldValue.increment(rating))
    db
        .collection("Clients")
        .document(id)
        .update("Completed Trips",FieldValue.increment(1))
}

fun toggleRunning(tripId: String, isTripDone: Boolean) {
    val db = Firebase.firestore

    // Create a map with the new values, including the "Running" field
    val updateMap = hashMapOf(
        "Running" to !isTripDone
        //"timestamp" to FieldValue.serverTimestamp() // You can include a timestamp field if needed
    )

    // Set the entire document in the "Trips" collection with the specified tripId
    db.collection("Trips")
        .document(tripId)
        .set(updateMap, SetOptions.merge()) // Use SetOptions.merge() to merge the new values with existing ones
        .addOnSuccessListener {
            // Handle success if needed
        }
        .addOnFailureListener { e ->
            // Handle failure if needed
        }
}