package com.abhishek.truckerbuddy.composables

import android.os.Build
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.AirplaneTicket
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.abhishek.truckerbuddy.FeedCallBack
import com.abhishek.truckerbuddy.R
import com.abhishek.truckerbuddy.TripBrief
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale



@Composable
fun TripCard(trip: TripBrief, modifier: Modifier = Modifier, onPlaceBidClick: () -> Unit = {}, str:String="Place Your Bid") {
    if(str=="Closed") toggleRunning(tripId = trip.tripId,isTripDone = true)
    var truckImage by remember {
        mutableStateOf("")
    }
    val db=Firebase.firestore
    db.collection("Trucks")
        .document(trip.truckType)
        .get()
        .addOnSuccessListener { document ->
            if(document!=null){
                truckImage= document.getString("Photo").toString()
            }
            else{

            }
        }
        .addOnFailureListener { e->

        }
    Card(
        modifier= Modifier
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "Pick Up Date",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    BlinkingText(
                        text = trip.pickUpDate,
                        color = Color.Red,
                        size = 12.0f
                    )
                    Text(
                        text = "Pick Up Time",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    BlinkingText(
                        text = trip.pickUpTime,
                        color = Color.Red,
                        size = 12.0f
                    )
                    Text(
                        text = "Pick Up Location",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = trip.pickUpLocation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Delivery Location",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = trip.deliveryLocation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
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
                        .padding(top = 8.dp)
                )
            }

            Text(
                text = "Truck Type: ${trip.truckType}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Capacity: ${trip.truckCapacity}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BlinkingButton(text = str, color = if(str=="Closed")Color.Red else Color(0xFF008B8B), onClick = onPlaceBidClick)
        }
    }
}

@Composable
fun BlinkingButton(text: String, color: Color, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "buttonColor")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = color,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonColor"
    )

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Bold,
            color = animatedColor,
            fontSize = 16.sp
        ),
        modifier = Modifier
            .padding(bottom = 8.dp)
            .clickable { onClick() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(feedCallBack: FeedCallBack, trips : List<TripBrief>) {

    var searchQuery by remember { mutableStateOf("") }
    val currentDateTime = LocalDateTime.now()



    val filteredTrips by remember(searchQuery, trips) {
        derivedStateOf {
            if (searchQuery.isBlank()) {

                trips.filter { trip ->

                    isPickUpDateTimeInFuture(trip.pickUpDate, trip.pickUpTime)
                }
            } else {

                trips.filter { trip ->
                    (trip.pickUpLocation.contains(searchQuery, ignoreCase = true) ||
                            trip.deliveryLocation.contains(searchQuery, ignoreCase = true)) &&
                            isPickUpDateTimeInFuture(trip.pickUpDate, trip.pickUpTime)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Feed")
                },
                actions = {

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue

                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"

                            )
                        },
                        placeholder = {
                            Text(text = "Search...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        )
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    BottomNavigationItem(
                        title = "Feed",
                        selectedIcon = Icons.AutoMirrored.Filled.AirplaneTicket,
                        unselectedIcon = Icons.AutoMirrored.Outlined.AirplaneTicket
                    ),
                    BottomNavigationItem(
                        title = "Post",
                        selectedIcon = Icons.Filled.Add,
                        unselectedIcon = Icons.Outlined.Add
                    ),
                    /*BottomNavigationItem(
                        title = "Settings",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings
                    ),*/
                    BottomNavigationItem(
                        title = "Profile",
                        selectedIcon = Icons.Filled.Face,
                        unselectedIcon = Icons.Outlined.Face
                    ),
                )

                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            if (selectedItemIndex == 1) {
                                feedCallBack.gotoPostScreen()
                            } else if (selectedItemIndex == 2) {
                                feedCallBack.gotoProfileScreen()
                            }
                        },
                        label = {
                            Text(text = item.title)
                        },
                        alwaysShowLabel = true,
                        icon = {

                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )

                        }
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 25.dp, start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(filteredTrips) {
                TripCard(trip = it, onPlaceBidClick = { feedCallBack.placeYourBid(it.tripId,it.creator) })
            }
        }
    }
}


@Composable
fun BlinkingText(
    text: String,
    color: Color,
    size: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "color")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = color,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Bold,
            color = animatedColor,
            fontSize = size.sp
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}



fun isPickUpDateTimeInFuture(pickUpDate: String, pickUpTime: String): Boolean {
    val dateTimeString = "$pickUpDate $pickUpTime"
    val dateFormat = SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.ENGLISH)

    return try {
        val pickUpDateTime = dateFormat.parse(dateTimeString)
        val currentTime = Date()
        pickUpDateTime?.after(currentTime) ?: false
    } catch (e: Exception) {
        false
    }
}
