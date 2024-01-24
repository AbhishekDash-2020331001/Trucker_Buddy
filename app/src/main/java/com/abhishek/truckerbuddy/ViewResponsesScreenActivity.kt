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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.Truck
import com.abhishek.truckerbuddy.composables.ViewResponsesScreen
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme

class ViewResponsesScreenActivity : ComponentActivity(),ViewResponsesScreenCallBack {
    private lateinit var receivedIntent: Intent
    private lateinit var tripId: String
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
                    ViewResponsesScreen(tripId = tripId)
                }
            }
        }
    }

    override fun showBidderProfile(bidderId: String) {
        TODO("Not yet implemented")
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