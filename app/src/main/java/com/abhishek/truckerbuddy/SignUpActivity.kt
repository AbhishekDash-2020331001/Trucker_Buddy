package com.abhishek.truckerbuddy

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.RegScreen
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : ComponentActivity(),SignUpCallBack {
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    var signUpCallBack=this
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegScreen(signUpCallBack)
                }
            }
        }
    }
    override fun register(email: String, password: String,name: String,username:String) {
        if (email=="" || password=="" || name=="" || username==""){
            Toast.makeText(
                baseContext,
                "Please fill up the fields correctly",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        val collectionPath = "Clients"

        val dbb = FirebaseFirestore.getInstance()

// Create a reference to the document
        val documentReference = dbb.collection(collectionPath).document(username)

        documentReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Document exists
                        val data: Map<String, Any>? = documentSnapshot.data
                        // Handle the data if needed
                        Toast.makeText(
                            baseContext,
                            "This username is already taken",
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        // Document does not exist
                        try {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success")
                                        val user = auth.currentUser
                                        updateUI(user)
                                        val uid= user?.uid
                                        val client= hashMapOf(
                                            "Name" to name,
                                            "Username" to username,
                                            "Email" to email,
                                            "Phone" to "Not Provided",
                                            "Password" to password,
                                            "Completed Trips" to 0,
                                            "Running Trips" to 0,
                                            "Score" to 0,
                                            "Rating" to 0,
                                            "Photo" to ""
                                        )
                                        if (uid != null) {
                                            db.collection("Clients").document(uid)
                                                .set(client, SetOptions.merge())
                                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                                        }
                                        val navigate=Intent(this@SignUpActivity,MainActivity::class.java)
                                        startActivity(navigate)
                                        finish()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            baseContext,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                        updateUI(null)
                                    }
                                }
                        }catch (e:Exception){
                            Toast.makeText(
                                baseContext,
                                "Please fill up the fields correctly",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                } else {
                    // An error occurred while retrieving the document
                }
            }


    }

    override fun goToLoginScreen() {
        val intent= Intent(this@SignUpActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    TruckerBuddyTheme {
        Greeting2("Android")
    }
}