package com.abhishek.truckerbuddy.composables

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhishek.truckerbuddy.R
import com.abhishek.truckerbuddy.TruckScreenCallBack
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun TruckScreen(truckScreenCallBack: TruckScreenCallBack){
    val db=Firebase.firestore
    var truckList by remember {
        mutableStateOf<List<Truck>>(emptyList())
    }

    db.collection("Trucks")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                truckList+=Truck(name = document.id, imageRes = document.getString("Photo")?:"", highestCapacity = document.getDouble("Capacity")
                    ?.toInt()
                    ?:0)
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "Error getting documents: ", exception)
        }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            items(truckList) { truck ->
                TruckCard(
                    truck = truck,
                    onclick= { truckScreenCallBack.gotoPostScreen(truck) },
                    default = R.drawable.truck
                )
                Spacer(modifier = Modifier.height(8.dp))
        }
    }
}