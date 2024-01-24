package com.abhishek.truckerbuddy.composables

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abhishek.truckerbuddy.R
import com.abhishek.truckerbuddy.TripDetailScreenCallBack
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTripDetail(trip: TripBrief, creator: String, creatorId: String, flag: Boolean) {
    var str by remember {
        mutableStateOf("")
    }
    var truckImage by remember {
        mutableStateOf("")
    }
    val db = Firebase.firestore
    val truckType = trip.truckType
    Log.d(TAG, "Ekhane $truckType")
    if (truckType.isNotBlank()) {
        Log.d(TAG, "Not Blank")
        db.collection("Trucks")
            .document(trip.truckType)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    truckImage = document.getString("Photo").toString()
                } else {

                }
            }
            .addOnFailureListener { e ->

            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Trip ID
            Text(
                text = "Trip ID",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trip.tripId,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Trip Creator Name
            Text(
                text = "Trip Creator",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            BlinkingButton(text = creator, color = Color(0xFF008B8B), onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Status
            Text(
                text = "Status",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (flag) "Running" else "Ended",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Pick Up Date and Time
            Text(
                text = "Pick Up Date and Time",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trip.pickUpDate,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Pick Up Location
            Text(
                text = "Pick Up Location",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trip.pickUpLocation,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Delivery Location
            Text(
                text = "Delivery Location",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trip.deliveryLocation,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Truck Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    val painter = rememberImagePainter(data = truckImage, builder = {
                        crossfade(true)
                        placeholder(R.drawable.flatbed)
                    })
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Truck Capacity
            Text(
                text = "Truck Capacity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = trip.truckCapacity.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Bid Text and TextField
            TextField(
                value = str, // Use state to hold the bid value
                onValueChange = {}, // Update state on value change
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            BlinkingButton(text = str, color = Color(0xFF008B8B), onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}*/

@Composable
fun TripDetailScreen(tripId:String,tripDetailScreenCallBack: TripDetailScreenCallBack) {
    // Replace with actual TripBrief data
    var creator by remember {
        mutableStateOf("")
    }
    var creatorId by remember {
        mutableStateOf("")
    }
    var pickUpTime by remember {
        mutableStateOf("")
    }
    var pickUpDivision by remember {
        mutableStateOf("")
    }
    var pickUpZilla by remember {
        mutableStateOf("")
    }
    var deliveryDivision by remember {
        mutableStateOf("")
    }
    var deliveryZilla by remember {
        mutableStateOf("")
    }
    var pickUpDate by remember {
        mutableStateOf("")
    }
    var truckCapacity by remember {
        mutableIntStateOf(0)
    }
    var goodsType by remember {
        mutableStateOf("")
    }
    var truckType by remember {
        mutableStateOf("")
    }
    var flag by remember{ mutableStateOf(false) }
    val db=Firebase.firestore
    LaunchedEffect(tripId) {
        try {
            val document = db.collection("Trips")
                .document(tripId)
                .get()
                .await()

            if (document != null) {
                pickUpDate = document.getString("Pick Up Date") ?: ""
                pickUpDivision = document.getString("Pick Up Division") ?: ""
                creator = document.getString("Post Creator Name") ?: ""
                creatorId = document.getString("Post Creator") ?: ""

                pickUpTime = document.getString("Pick Up Time") ?: ""
                pickUpZilla = document.getString("Pick Up Zilla") ?: ""
                deliveryDivision = document.getString("Delivery Division") ?: ""
                deliveryZilla = document.getString("Delivery Zilla") ?: ""
                goodsType = document.getString("Type of Good") ?: ""
                flag = document.getBoolean("Running") == true

                val truckMap = document.get("Needed Truck") as? Map<String, Any>
                if (truckMap != null) {
                    // Retrieve values associated with keys
                    truckType = truckMap["name"] as? String ?: ""
                    truckCapacity = truckMap["highestCapacity"] as? Int ?: 0
                } else {
                    // Handle the case where "Truck" is not a map or is null
                    println("Truck field is not a map or is null.")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching trip details: ${e.message}")
        }
    }

    // Call ShowTripDetail inside LaunchedEffect
    ShowTripDetail(
        trip = TripBrief(
            tripId = tripId,
            pickUpTime = pickUpTime,
            pickUpLocation = "$pickUpDivision, $pickUpZilla",
            pickUpDate = pickUpDate,
            truckCapacity = truckCapacity,
            goodsType = goodsType,
            truckType = truckType,
            deliveryLocation = "$deliveryDivision, $deliveryZilla"
        ),
        creator = creator,
        creatorId = creatorId,
        flag = flag,
        tripDetailScreenCallBack=tripDetailScreenCallBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTripDetail(trip: TripBrief, creator: String, creatorId: String, flag: Boolean,tripDetailScreenCallBack: TripDetailScreenCallBack) {
    var str by remember {
        mutableStateOf("")
    }

    var truckImage by remember {
        mutableStateOf("")
    }
    val db = Firebase.firestore
    val truckType = trip.truckType
    Log.d(TAG, "Ekhane $truckType")
    if (truckType.isNotBlank()) {
        Log.d(TAG, "Not Blank")
        db.collection("Trucks")
            .document(trip.truckType)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    truckImage = document.getString("Photo").toString()
                } else {

                }
            }
            .addOnFailureListener { e ->

            }
    }



            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp)
            ) {
                // First Box: Trip Information and Image
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier=Modifier
                            .padding(10.dp)
                            .wrapContentSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(10.dp)
                        /*modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)*/
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Trip ID
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Trip ID",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = trip.tripId.takeLast(16),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Trip Creator",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                BlinkingButton(text = creator, color = Color(0xFF008B8B), onClick = {})
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Status",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if(flag) BlinkingText(text = "Running", color = Color(0xFF008B8B), size = 12f)
                                else BlinkingText(text = "Ended", color = Color.Red, size = 12f)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Pick Up Date",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                BlinkingText(text = trip.pickUpDate,
                                    color = if(flag) Color(0xFF008B8B) else Color.Red,
                                    size = 12f
                                )

                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Pick Up Time",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                BlinkingText(
                                    text = trip.pickUpTime,
                                    color = if(flag) Color(0xFF008B8B) else Color.Red,
                                    size = 12f
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Pick Up Location",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = trip.pickUpLocation,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Delivery Location",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = trip.deliveryLocation,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Second Box: Bid Section
                    Spacer(modifier=Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Currency Icon
                            Icon(
                                painter = painterResource(id = R.drawable.ic_currency),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Place Bid Text
                            Text(
                                text = "Place Bid",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Bid TextField
                            OutlinedTextField(
                                value = str,
                                onValueChange = { str = it },
                                leadingIcon = { Icon(imageVector = Icons.Default.AddBusiness, null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = Color.Black, // Set the text color to black
                                    cursorColor = Color.Black,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Confirm Bid Button
                            Button(
                                onClick = {
                                    // Handle bid confirmation logic here
                                    // For example, you can print the bid value
                                    println("Bid confirmed: $str")
                                    tripDetailScreenCallBack.placeBid(tripId = trip.tripId, tripCreatorId = creatorId, bidAmount = str)

                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = "Confirm Bid")
                            }
                        }

                }



            }




}

