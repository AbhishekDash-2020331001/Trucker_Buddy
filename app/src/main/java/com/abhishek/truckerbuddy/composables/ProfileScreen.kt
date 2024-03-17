package com.abhishek.truckerbuddy.composables

import android.content.ContentValues
import android.net.Uri
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import coil.compose.rememberImagePainter
import com.abhishek.truckerbuddy.ProfileCallBack
import com.abhishek.truckerbuddy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

data class UserProfile(
    val profilePictureUrl: String?,
    val name: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val completedTrips: Int,
    val runningTrips: Int,
    val userRating: Double,
    val emailVerified: Boolean,
    val deals: Int,
    val coin: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(userProfile: UserProfile,profileCallBack: ProfileCallBack) {
    val auth=Firebase.auth
    var url by remember{
        mutableStateOf(userProfile.profilePictureUrl)
    }
    val currentUser=auth.currentUser
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
        mutableIntStateOf(3)
    }

    val initialIndex = remember { selectedItemIndex }

    Scaffold(
        bottomBar = {
            NavigationBar() {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            // navController.navigate(item.title)
                            if(selectedItemIndex==1){
                                profileCallBack.gotoPost()
                            }
                            else if(selectedItemIndex==0){
                                profileCallBack.gotoFeed()
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
    ) {innerPadding ->
        var selectedImageUri by remember {
            mutableStateOf<Uri?>(null)
        }

        var isLoading by remember { mutableStateOf(false) }

        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                isLoading = true
                selectedImageUri = uri
                try {
                    upload(selectedImageUri) { downloadUrl ->
                        // Use the downloadUrl as needed
                        if (downloadUrl.isNotEmpty()) {
                            // Image upload and Firestore update successful
                            url = downloadUrl
                        } else {
                            // Handle the case when downloadUrl is empty (upload or Firestore update failed)
                        }
                        isLoading = false
                    }
                }catch (e:Exception){
                    println("Hello")
                }

            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = (bottomPadding + 25).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            profileCallBack.showToast(userProfile.coin)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00FFFF)) // Background color for the circle
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Coins",
                            tint = Color.Yellow, // Color of the star icon
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${userProfile.coin}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 24.sp, // Adjust the font size as needed
                            fontWeight = FontWeight.Bold, // Apply a bold font weight
                            fontStyle = FontStyle.Normal,
                        ),
                        color = Color(0xFFFFD700) // Color for the text
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                ProfilePicture(url, isLoading)
                Spacer(modifier = Modifier.height(16.dp))
                ElevatedButton(onClick = { singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    Log.d(TAG,"Entering2")
                    Log.d(TAG, "Selected Image Uri: $selectedImageUri")

                },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Change Profile Picture", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(16.dp))
                UserDetailCard(
                    icon = Icons.Default.Person,
                    label = "Name",
                    value = userProfile.name,
                    onClick={}
                )
                /*UserDetailCard(
                    icon = Icons.Default.Person,
                    label = "Username",
                    value = userProfile.username,
                    onClick={}
                )*/

                EmailCard(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = userProfile.email,
                    onClick={profileCallBack.sendVerificationMail()},
                    emailVerified = userProfile.emailVerified,
                    currentUser=currentUser
                )

                UserDetailCard(
                    icon = Icons.Default.Phone,
                    label = "Phone Number",
                    value = userProfile.phoneNumber,
                    onClick={}
                )

                /*UserDetailCard(
                    icon = Icons.Default.TripOrigin,
                    label = "Completed Trips",
                    value = userProfile.completedTrips.toString(),
                    onClick={}
                )*/

                UserDetailCard(
                    icon = Icons.Default.WorkOutline,
                    label = "My Trips",
                    value = userProfile.runningTrips.toString(),
                    onClick={
                        profileCallBack.myRunningTrips()
                    }
                )
                UserDetailCard(
                    icon = Icons.AutoMirrored.Filled.Announcement,
                    label = "Received Deal Request",
                    value = userProfile.deals.toString(),
                    onClick = {
                        profileCallBack.gotoReceivedBid()
                    }
                )
                UserDetailCard(
                    icon = Icons.Default.Star,
                    label = "User Rating",
                    value = userProfile.userRating.toString(),
                    onClick={}
                )
                Button(
                    onClick = {
                              profileCallBack.signOut()
                    }, shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "LOGOUT",
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Normal,
                            letterSpacing = 2.sp,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }


    }



}

fun upload(selectedImageUri: Uri?, callback: (String) -> Unit) {
    val db: FirebaseFirestore = Firebase.firestore
    val storage: FirebaseStorage = Firebase.storage
    val auth: FirebaseAuth = Firebase.auth
    val storageRef: StorageReference = storage.reference
    val currentUser = auth.currentUser
    val uid = currentUser?.uid
    val fileName = uid + "_profile_image.jpg"
    val imageRef: StorageReference = storageRef.child(fileName)

    // Upload the image to Firebase Storage
    imageRef.putFile(selectedImageUri!!)
        .addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, now you can get the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // The URI contains the download URL of the uploaded image
                val downloadUrl = uri.toString()
                val client = hashMapOf(
                    "Photo" to downloadUrl
                )
                if (uid != null) {
                    db.collection("Clients").document(uid)
                        .set(client, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                            callback(downloadUrl)
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error writing document", e)
                            callback("") // Return empty string in case of failure
                        }
                } else {
                    callback("") // Return empty string if UID is null
                }
            }
        }
        .addOnFailureListener { exception ->
            // Handle the failure of the image upload
            Log.e(ContentValues.TAG, "Error uploading image to Firebase Storage: ${exception.message}")
            callback("") // Return empty string in case of failure
        }
}
@Composable
fun EmailCard(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    emailVerified: Boolean,
    currentUser: FirebaseUser?
){
    Card(
        modifier= Modifier
            //.padding(top=8.dp)
            .padding(4.dp)
            .wrapContentSize()
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(10.dp)
        /*modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .clickable(onClick = onClick) */// Updated clickable modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                Row (
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    if (currentUser != null) {
                        BlinkingButton(
                            text = if(emailVerified)"Verified" else "Verify Your Mail",
                            color = if(currentUser.isEmailVerified)Color(0xFF008B8B) else Color.Red,
                            onClick=onClick
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun UserDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier= Modifier
            //.padding(top=8.dp)
            .padding(4.dp)
            .wrapContentSize()
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(10.dp)
        /*modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .clickable(onClick = onClick) */// Updated clickable modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}


@Composable
fun ProfilePicture(profilePictureUrl: String?, isDataLoading: Boolean) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
    ) {
        // Use Coil's rememberImagePainter to load the image from the URL
        val painter = rememberImagePainter(data = profilePictureUrl, builder = {
            crossfade(true)
            placeholder(R.drawable.pp) // Replace with a placeholder image resource
        })

        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
        )

        // Circular loading indicator
        if (isDataLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}



@Composable
fun ProfileScreen(
    profilePictureUrl: String?,
    name: String,
    username: String,
    email: String,
    phoneNumber: String,
    completedTrips: Int,
    runningTrips: Int,
    userRating: Double,
    emailVerified:Boolean,
    deals:Int,
    coin:Int,
    profileCallBack: ProfileCallBack) {
    val userProfile = UserProfile(
        profilePictureUrl = profilePictureUrl,
        name = name,
        username = username,
        email = email,
        phoneNumber = phoneNumber,
        completedTrips = completedTrips,
        runningTrips = runningTrips,
        userRating = userRating,
        emailVerified=emailVerified,
        deals=deals,
        coin = coin
    )
    UserProfileScreen(userProfile = userProfile,profileCallBack=profileCallBack)
}





