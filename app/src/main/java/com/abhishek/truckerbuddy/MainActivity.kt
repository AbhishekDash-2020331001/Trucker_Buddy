package com.abhishek.truckerbuddy

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.abhishek.truckerbuddy.composables.FeedScreen
import com.abhishek.truckerbuddy.composables.LoginScreen
import com.abhishek.truckerbuddy.composables.PostScreen
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity(),LoginCallBack {
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    var loginCallBack=this
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent=Intent(this@MainActivity,ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize Firebase Auth
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                LoginScreen(loginCallBack)
                //ProfileScreen()
                //PostScreen()
                //FeedScreen()
                //BeautifulCardExample()
            }
        }
    }

    override fun doSignIn(email: String, password: String) {
        if(email=="" || password==""){
            Toast.makeText(
                baseContext,
                "Please fill up the fields",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    val intent=Intent(this@MainActivity,ProfileActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    override fun goToRegScreen() {
        val intent= Intent(this@MainActivity,SignUpActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun reload(){

}

fun updateUI(user:FirebaseUser?){

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TruckerBuddyTheme {
        Greeting("Android")
    }
}