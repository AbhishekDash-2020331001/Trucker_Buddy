package com.abhishek.truckerbuddy.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhishek.truckerbuddy.MyRunningTripsCallBack

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
fun MyRunningTrips(myRunningTripsCallBack: MyRunningTripsCallBack,trips:List<TripBrief>){
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (trips.isEmpty()) {
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
            items(trips) {
                TripCard(trip = it, str = "View Responses", onPlaceBidClick = {myRunningTripsCallBack.viewResponses(it.tripId)})

            }
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

