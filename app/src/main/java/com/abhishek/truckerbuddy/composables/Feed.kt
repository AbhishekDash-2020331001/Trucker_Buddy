package com.abhishek.truckerbuddy.composables

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import coil.compose.rememberImagePainter
import com.abhishek.truckerbuddy.FeedCallBack
import com.abhishek.truckerbuddy.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class TripBrief(
    val pickUpTime:String,
    val tripId:String,
    val pickUpDate: String,
    val pickUpLocation: String,
    val deliveryLocation: String,
    val truckType: String,
    val truckCapacity: Int,
    val goodsType: String
)

@Composable
fun TripCard(trip: TripBrief, modifier: Modifier = Modifier, onPlaceBidClick: () -> Unit = {},str:String="Place Your Bid") {
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
        modifier=Modifier
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
            }

            // Truck type and capacity
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

            // Place your bid button with blinking animation
            BlinkingButton(text = str, color = Color(0xFF008B8B), onClick = onPlaceBidClick)
        }
    }
}

@Composable
fun BlinkingButton(text: String, color: Color, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "buttonColor")

    // Animation parameters
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(feedCallBack: FeedCallBack) {
    var truckImg by remember{ mutableStateOf("") }
    var trips by remember { mutableStateOf<List<TripBrief>>(emptyList()) }
    val db=Firebase.firestore
    db.collection("Trips")
        .whereEqualTo("Running", true)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d(TAG, "${document.id} => ${document.data}")
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

    val density = LocalDensity.current.density
    val bottomPadding = with(LocalView.current) {
        if (isAttachedToWindow) {
            val insets = ViewCompat.getRootWindowInsets(this)?.systemGestureInsets
            insets?.bottom?.toFloat()?.div(density) ?: 0f
        } else {
            0f
        }
    }
    val items = listOf(
        BottomNavigationItem(
            title = "Feed",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "Post",
            selectedIcon = Icons.Filled.Add,
            unselectedIcon = Icons.Outlined.Add
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Face,
            unselectedIcon = Icons.Outlined.Face
        ),
    )

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            // navController.navigate(item.title)
                            if(selectedItemIndex==1){
                                feedCallBack.gotoPostScreen()
                            }
                            else if(selectedItemIndex==3){
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
        }
    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = (bottomPadding + 25).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(trips) {
                TripCard(trip = it, onPlaceBidClick = { feedCallBack.placeYourBid(it.tripId) })
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

    // Animation parameters
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