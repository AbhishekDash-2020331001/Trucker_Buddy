package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.FeedScreen
import com.abhishek.truckerbuddy.composables.Truck
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme

class FeedActivity : ComponentActivity(),FeedCallBack {
    val feedCallBack=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FeedScreen(feedCallBack)
                }
            }
        }
    }

    override fun placeYourBid(tripId:String) {
        val intent= Intent(this@FeedActivity,TripDetailScreenActivity::class.java)
        intent.putExtra("tripId", tripId)
        startActivity(intent)
    }

    override fun gotoPostScreen() {
        val truck= Truck(
            name = "Select",
            imageRes = "https://firebasestorage.googleapis.com/v0/b/trucker-buddy-f7323.appspot.com/o/truck.jpg?alt=media&token=e1675e46-78f8-471b-8b3f-18218f5d6cee",
            highestCapacity = 0
        )
        val intent= Intent(this@FeedActivity,PostActivity::class.java)
        intent.putExtra("selectedTruck",truck)
        startActivity(intent)
        finish()
    }

    override fun gotoProfileScreen() {
        val intent= Intent(this@FeedActivity,ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun Greeting7(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview7() {
    TruckerBuddyTheme {
        Greeting7("Android")
    }
}