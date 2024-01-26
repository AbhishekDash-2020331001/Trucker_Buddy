package com.abhishek.truckerbuddy.composables

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abhishek.truckerbuddy.ViewResponsesScreenCallBack
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.tasks.await

data class Bid(val bidAmount: String, val bidderId: String, val creatorId: String, val tripId: String)
data class BidWithName(val bid:Bid,val name:String)

suspend fun getName(id: String): String {
    val db = Firebase.firestore
    val documentSnapshot = db.collection("Clients")
        .document(id)
        .get()
        .await()

    // Replace "Name" with the actual field name used in your Firestore document
    return documentSnapshot.getString("Name") ?: ""
}

@Composable
fun BidderCard(bids: List<Bid>, viewResponsesScreenCallBack: ViewResponsesScreenCallBack) {
    var bidds by remember {
        mutableStateOf<List<BidWithName>>(emptyList())
    }
    for (bid in bids){
        LaunchedEffect(bid){
            val name=getName(bid.bidderId)
            bidds+=BidWithName(bid=bid,name=name)
        }
    }
    Log.d(TAG, bidds.size.toString())
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize()
    ) {
        if (bidds.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(8.dp)
                    )
                }
            }
        }
        else{
            items(bidds) { bid ->
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
                            .fillMaxSize()
                            .padding(5.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Bidder's Name Row with Show Bidder Profile Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = bid.name, // Change this to the actual field you want to display
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Button(
                                onClick = {
                                    viewResponsesScreenCallBack.showBidderProfile(bid.bid.bidderId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008B8B)),
                                contentPadding = PaddingValues(8.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "Show Bidder Profile", style = MaterialTheme.typography.bodyMedium)
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Bid Amount Row
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start=4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bid Amount:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = bid.bid.bidAmount, // Change this to the actual field you want to display
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

    }
}



@OptIn(DelicateCoroutinesApi::class)
suspend fun getDocuments(tripId: String): List<DocumentSnapshot> {
    val db = Firebase.firestore
    return db.collection("Bids")
        .whereEqualTo("Trip Id", tripId)
        .get()
        .await()
        .documents
}


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ViewResponsesScreen(bids: List<Bid>, viewResponsesScreenCallBack: ViewResponsesScreenCallBack) {

    val c=bids.size
    Log.d(TAG,"harder $c")
    BidderCard(bids = bids, viewResponsesScreenCallBack = viewResponsesScreenCallBack)
}

