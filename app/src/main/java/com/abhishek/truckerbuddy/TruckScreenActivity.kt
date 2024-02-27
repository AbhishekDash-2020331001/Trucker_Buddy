package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.LocalTime

class TruckScreenActivity : ComponentActivity(),TruckScreenCallBack {
    private lateinit var receivedIntent: Intent
    private lateinit var ptime: LocalTime
    private lateinit var pdate: LocalDate
    val truckScreenCallBack=this
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receivedIntent = intent
        ptime = (receivedIntent.getSerializableExtra("ptime") as? LocalTime)!!
        pdate = (receivedIntent.getSerializableExtra("pdate") as? LocalDate)!!
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun gotoPostScreen(truck: Truck) {
        val intent= Intent(this@TruckScreenActivity,PostActivity::class.java)
        intent.putExtra("selectedTruck", truck)
        intent.putExtra("ptime",ptime)
        intent.putExtra("pdate",pdate)
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