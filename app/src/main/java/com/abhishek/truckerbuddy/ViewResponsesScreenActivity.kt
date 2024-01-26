package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.Bid
import com.abhishek.truckerbuddy.composables.Truck
import com.abhishek.truckerbuddy.composables.ViewResponsesScreen
import com.abhishek.truckerbuddy.composables.getDocuments
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme

class ViewResponsesScreenActivity : ComponentActivity(),ViewResponsesScreenCallBack {
    private lateinit var receivedIntent: Intent
    private lateinit var tripId: String
    var viewResponsesScreenCallBack=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receivedIntent = intent
        tripId = (receivedIntent.getSerializableExtra("tripId") as? String)?:""
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d(TAG, tripId.length.toString())
                    var bids by remember {
                        mutableStateOf<List<Bid>>(emptyList())
                    }

                    LaunchedEffect(tripId) {
                        val documents = getDocuments(tripId)
                        for (document in documents){
                            val bidAmount = document.getString("Bid Amount") ?: ""
                            val bidderId = document.getString("Bidder Id") ?: ""
                            val creatorId = document.getString("Creator Id") ?: ""
                            val trippId = document.getString("Trip Id") ?: ""
                            bids+= Bid(bidAmount, bidderId, creatorId, trippId)
                        }
                    }
                    ViewResponsesScreen(bids = bids, viewResponsesScreenCallBack = viewResponsesScreenCallBack)
                }
            }
        }
    }

    override fun showBidderProfile(bidderId: String) {
        val intent=Intent(this@ViewResponsesScreenActivity,BidderProfileScreenActivity::class.java)
        intent.putExtra("bidderId",bidderId)
        startActivity(intent)
    }
}

@Composable
fun Greeting9(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview9() {
    TruckerBuddyTheme {
        Greeting9("Android")
    }
}