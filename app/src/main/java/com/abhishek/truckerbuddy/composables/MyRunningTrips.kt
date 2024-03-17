package com.abhishek.truckerbuddy.composables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhishek.truckerbuddy.MyRunningTripsCallBack
import com.abhishek.truckerbuddy.TripBrief
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyRunningTrips(myRunningTripsCallBack: MyRunningTripsCallBack, trips: List<TripBrief>) {
    // Separate running and non-running trips
    val (runningTrips, nonRunningTrips) = trips.partition { isPickUpDateTimeInFuture(pickUpDate = it.pickUpDate, pickUpTime = it.pickUpTime, currentDateTime = LocalDateTime.now()) }

    val context = LocalContext.current

    Scaffold(
        /*topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                ),
                title = { Text("My Trips") },
                modifier = Modifier.padding(start=4.dp, bottom = 4.dp,end=4.dp)
            )
        },*/
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
            }
        }
    )
}
