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
import com.abhishek.truckerbuddy.composables.Truck
import com.abhishek.truckerbuddy.composables.TruckScreen
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme

class TruckScreenActivity : ComponentActivity(),TruckScreenCallBack {
    val truckScreenCallBack=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TruckScreen(truckScreenCallBack)
                }
            }
        }
    }

    override fun gotoPostScreen(truck: Truck) {
        val intent= Intent(this@TruckScreenActivity,PostActivity::class.java)
        intent.putExtra("selectedTruck", truck)
        startActivity(intent)
    }
}

@Composable
fun Greeting5(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    TruckerBuddyTheme {
        Greeting5("Android")
    }
}