package com.abhishek.truckerbuddy.composables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.abhishek.truckerbuddy.MyRunningTripsCallBack
import com.abhishek.truckerbuddy.TripBrief
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyRunningTrips(myRunningTripsCallBack: MyRunningTripsCallBack, trips: List<TripBrief>) {
    val (runningTrips, nonRunningTrips) = trips.partition { isPickUpDateTimeInFuture(pickUpDate = it.pickUpDate, pickUpTime = it.pickUpTime) }

    val context = LocalContext.current

    Scaffold(
        content = {innerpadding->
            LazyColumn(
                modifier = Modifier
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = rememberLazyListState()
            ) {


                    if (runningTrips.isNotEmpty()) {
                        items(runningTrips) {
                            MyRunningTripsCard(
                                trip = it,
                                str = if(it.ongoing)"Trip Ongoing" else "View Responses",
                                onPlaceBidClick = { myRunningTripsCallBack.viewResponses(it.tripId) }
                            )
                        }
                    }
                    if (nonRunningTrips.isNotEmpty()) {
                        items(nonRunningTrips) {
                            MyRunningTripsCard(
                                trip = it,
                                str = "Closed",
                                onPlaceBidClick = { myRunningTripsCallBack.viewResponses(it.tripId) }
                            )
                        }
                    }
                    if (trips.isEmpty()) {
                        item {
                            NoTripAvailablePage()
                        }
                    }






            }
        }
    )
}
