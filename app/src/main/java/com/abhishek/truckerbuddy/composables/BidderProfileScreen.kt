package com.abhishek.truckerbuddy.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.abhishek.truckerbuddy.BidderInfo
import com.abhishek.truckerbuddy.BidderProfileScreenActivityCallBack
import com.abhishek.truckerbuddy.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun BidderProfileScreen(bidderInfo: BidderInfo, bidId: String, bidderId: String,bidderProfileScreenActivityCallBack: BidderProfileScreenActivityCallBack) {
    var dealDue by remember {
        mutableStateOf(true)
    }
    var dealSent by remember {
        mutableStateOf(false)
    }
    var driverReplied by remember {
        mutableStateOf(false)
    }
    var dealAccepted by remember {
        mutableStateOf(false)
    }
    val db = Firebase.firestore
    val currentUserUid= Firebase.auth.currentUser?.uid
    db.collection("Bids")
        .document(bidId)
        .get()
        .addOnSuccessListener { document ->
            dealDue = document.getBoolean("Deal Due") == true
            dealSent = document.getBoolean("Deal Sent") == true
            driverReplied = document.getBoolean("Driver Replied") == true
            dealAccepted = document.getBoolean("Deal Accepted") == true
        }
        .addOnFailureListener {

        }

    Column(
        modifier = Modifier
            .wrapContentSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Bidder Information",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

        Card(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val painter = rememberImagePainter(
                        data = bidderInfo.photo,
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.pp)
                        }
                    )

                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(75.dp)
                    )

                    RowWithText("Bidder", bidderInfo.name)
                    RowWithText("Rating", bidderInfo.rating.toString())
                    when {
                        dealDue -> {
                            Button(
                                onClick = {
                                    dealDue=false
                                    dealSent=true
                                    if (currentUserUid != null) {
                                        bidderProfileScreenActivityCallBack.sendDeal(bidId = bidId, bidderId = bidderId, currentUserUid = currentUserUid)
                                    }

                                }) {
                                Text(text = "Send Deal Request")
                            }
                        }
                        dealSent && !driverReplied -> {
                            Button(
                                onClick = {
                                    dealDue=true
                                    dealSent=false
                                    if (currentUserUid != null) {
                                        bidderProfileScreenActivityCallBack.cancelDeal(bidId = bidId, bidderId = bidderId, currentUserUid = currentUserUid)
                                    }
                                }) {
                                Text(text = "Cancel Deal")
                            }
                        }
                        driverReplied && !dealAccepted -> {
                            BlinkingText(text = "Deal Rejected", color = Color.Red, size = 12f)
                        }
                        driverReplied && dealAccepted -> {
                            BlinkingText(text = "Deal Accepted", color= Color(0xFF008B8B), size=12f)
                        }
                    }
                }
            }
        }
        if (dealAccepted) {
            ShowContactInformationCard(bidderInfo)
        }
    }
}

@Composable
private fun ShowContactInformationCard(bidderInfo: BidderInfo) {
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Contact Information",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            RowWithText("Email", bidderInfo.email)
            RowWithText("Phone", bidderInfo.phone)
        }
    }
}



@Composable
private fun RowWithText(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}