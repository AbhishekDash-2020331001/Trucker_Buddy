package com.abhishek.truckerbuddy.composables

import android.os.Build
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.automirrored.outlined.AirplaneTicket
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import coil.compose.rememberImagePainter
import com.abhishek.truckerbuddy.PostCallBack
import com.abhishek.truckerbuddy.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostScreen(postCallBack: PostCallBack,truck: Truck, ptime: LocalTime, pdate: LocalDate){
    val goods= listOf("Clothing","Electronics and Home Appliances","Furnitures and Household Items","Food and Beverages","Timber and Wood Products","Metal and Steel","Construction Materials","Automobiles ( Car, Truck, Animal )","Industrial Chemicals","Pharmaceuticals","Coal/Sand/Gravel","Petroleum Products","Cattle/Poultry/Other Livestock","Textiles","Garbage/Waste/Recyclable Materials","Construction Equipment","Products for Supermarkets and Retail Stores","Parcels and Packages for Delivery")
    var divisions by remember { mutableStateOf(emptyList<String>()) }
    var pickZillas by remember { mutableStateOf(emptyList<String>()) }
    var deliveryZillas by remember { mutableStateOf(emptyList<String>()) }
    val db = Firebase.firestore
    LaunchedEffect(Unit ){
        db.collection("Divisions")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    divisions+=document.id
                    Log.d(TAG,"checking")
                    Log.d(TAG,"Size ${divisions.size}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

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
        mutableStateOf(1)
    }

    var pickedDate by remember {
        mutableStateOf(pdate)
    }
    var pickedTime by remember {
        mutableStateOf(ptime)
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
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MMM dd yyyy")
                .format(pickedDate)
        }
    }
    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm a")
                .format(pickedTime)
        }
    }
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    var expanded1 by remember {
        mutableStateOf(false)
    }
    var pickUpDivision by remember {
        mutableStateOf("")
    }
    var typeOfGood by remember {
        mutableStateOf("")
    }
    var expanded2 by remember {
        mutableStateOf(false)
    }
    var pickUpZilla by remember { mutableStateOf<String?>(null) }
    var deliveryZilla by remember { mutableStateOf<String?>(null) }
    var expanded3 by remember {
        mutableStateOf(false)
    }
    var deliveryDivision by remember {
        mutableStateOf("")
    }
    var expanded4 by remember {
        mutableStateOf(false)
    }

    var expanded5 by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(pickUpDivision) {
        if (pickUpDivision.isNotEmpty()) {
            pickUpZilla = null
            val ref = db.collection("Divisions").document(pickUpDivision)
            try {
                val doc = ref.get().await()
                if (doc.exists()) {
                    val array = doc.get("Zilla") as? List<String>
                    if (array != null) {
                        pickZillas = array.toList()
                        Log.d(TAG, "Size ${pickZillas.size}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting document: $e")
            }
        }
    }

    LaunchedEffect(deliveryDivision) {
        if (deliveryDivision.isNotEmpty()) {
            deliveryZilla = null
            val ref = db.collection("Divisions").document(deliveryDivision)
            try {
                val doc = ref.get().await()
                if (doc.exists()) {
                    val array = doc.get("Zilla") as? List<String>
                    if (array != null) {
                        deliveryZillas = array.toList()
                        Log.d(TAG, "Size ${deliveryZillas.size}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting document: $e")
            }
        }
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
                            if(selectedItemIndex==0){
                                postCallBack.gotoFeed()
                            }
                            else if(selectedItemIndex==2){
                                postCallBack.gotoProfileScreen()
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
    ){innerPadding->
        LazyColumn(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .padding(bottom = (bottomPadding + 25).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item {
                Card(
                    modifier= Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .clickable {
                            dateDialogState.show()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null)
                            Text(text = "Pick-Up Date")
                        }
                        Text(text = formattedDate)
                    }
                }

                Card(
                    modifier= Modifier
                        .padding(bottom = 10.dp)
                        .wrapContentSize()
                        .fillMaxWidth()
                        .clickable {
                            timeDialogState.show()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.HourglassTop, contentDescription = null)
                            Text(text = "Pick-Up Time")
                        }
                        Text(text = formattedTime)
                    }
                }
                Spacer(modifier=Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .height(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .height(1.dp)
                            .fillMaxWidth(0.33f)
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.background)
                            .clip(MaterialTheme.shapes.medium)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Needed Type of Truck",
                            color = contentColorFor(MaterialTheme.colorScheme.background),

                            )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .height(1.dp)
                            .fillMaxWidth(0.5f)
                    )
                }

                Spacer(modifier=Modifier.height(32.dp))

                TruckCard(
                    truck = truck,
                    onclick = { postCallBack.gotoTruckScreen(ptime=pickedTime,pdate=pickedDate) },
                    default = R.drawable.truck
                )

                Spacer(modifier=Modifier.height(16.dp))


                ExposedDropdownMenuBox(
                    expanded = expanded5,
                    onExpandedChange = {expanded5=it} ) {
                    OutlinedTextField(
                        value = typeOfGood.ifEmpty { "Select Type of Good" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded5)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black, // Set the text color to black
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded5,
                        onDismissRequest = { expanded5=false }
                    ) {
                        goods.forEach{
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    typeOfGood=it
                                    expanded5=false
                                }
                            )
                        }

                    }
                }

                Spacer(modifier=Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .height(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .height(1.dp)
                            .fillMaxWidth(0.33f)
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.background)
                            .clip(MaterialTheme.shapes.medium)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Pick Up Location",
                            color = contentColorFor(MaterialTheme.colorScheme.background),

                            )
                    }


                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .height(1.dp)
                            .fillMaxWidth(0.5f)
                    )
                }

                Spacer(modifier=Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded1,
                    onExpandedChange = {expanded1=it} ) {
                    OutlinedTextField(
                        value = pickUpDivision.ifEmpty { "Select Division" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded1,
                        onDismissRequest = { expanded1=false }
                    ) {
                        divisions.forEach{
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    pickUpDivision=it
                                    expanded1=false
                                }
                            )
                        }

                    }
                }

                Spacer(modifier=Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded2,
                    onExpandedChange = {expanded2=it} ) {
                    OutlinedTextField(
                        value = pickUpZilla ?: "Select Zilla",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded2,
                        onDismissRequest = { expanded2=false }
                    ) {
                        pickZillas.forEach{
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    pickUpZilla=it
                                    expanded2=false }
                            )
                        }

                    }
                }

                Spacer(modifier=Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .height(1.dp)
                            .fillMaxWidth(0.33f)
                    )


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.background)
                            .clip(MaterialTheme.shapes.medium)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Delivery Location",
                            color = contentColorFor(MaterialTheme.colorScheme.background),

                            )
                    }


                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .height(1.dp)
                            .fillMaxWidth(0.5f)
                    )
                }

                Spacer(modifier=Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded3,
                    onExpandedChange = {expanded3=it} ) {
                    OutlinedTextField(
                        value = deliveryDivision.ifEmpty { "Select Division" },
                        onValueChange = {
                                        deliveryZilla= null.toString()
                        },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded3)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded3,
                        onDismissRequest = { expanded3=false }
                    ) {
                        divisions.forEach{
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    deliveryDivision=it
                                    expanded3=false
                                }
                            )
                        }

                    }
                }

                Spacer(modifier=Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded4,
                    onExpandedChange = {expanded4=it} ) {
                    OutlinedTextField(
                        value = deliveryZilla ?: "Select Zilla",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded4)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded4,
                        onDismissRequest = { expanded4=false }
                    ) {
                        deliveryZillas.forEach{
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    deliveryZilla=it
                                    expanded4=false }
                            )
                        }

                    }
                }

                Spacer(modifier=Modifier.height(16.dp))

                Button(
                    onClick = {
                              println("pickmoment $formattedDate and $formattedTime")
                              postCallBack.createPost(
                                  pickUpDate = formattedDate,
                                  pickUpTime = formattedTime,
                                  pickUpDivision= pickUpDivision,
                                  pickUpZilla= pickUpZilla ?: "Select Zilla",
                                  deliveryDivision= deliveryDivision,
                                  deliveryZilla = deliveryZilla ?: "Select Zilla",
                                  typeOfGood = typeOfGood
                              )
                    },
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Create Post",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Create Post")
                }
            }
        }
    }
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok") {

            }
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date",
            allowedDateValidator = {
                it.dayOfMonth>=LocalDate.now().dayOfMonth
            }
        ) {
            pickedDate = it
        }
    }
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok") {

            }
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.NOON,
            title = "Pick a time",
            timeRange = LocalTime.now() ..LocalTime.MAX
        ) {
            pickedTime = it
        }
    }
}

data class Truck(val name: String, val imageRes: String, val highestCapacity: Int):Serializable

@Composable
fun TruckCard(truck: Truck, onclick: () -> Unit,default:Int) {
    val painter = rememberImagePainter(data = truck.imageRes, builder = {
        crossfade(true)
        placeholder(drawableResId = default)
    })
    Card(
        modifier= Modifier
            .padding(8.dp)
            .wrapContentSize()
            .clickable {
                onclick.invoke()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.background)
            )

            Spacer(modifier = Modifier.width(16.dp))


            Column {

                Text(
                    text = truck.name,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                )

                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "Highest Capacity: ${truck.highestCapacity}",
                    color = contentColorFor(MaterialTheme.colorScheme.background),
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}


