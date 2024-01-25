package com.abhishek.truckerbuddy

import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
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
import com.abhishek.truckerbuddy.composables.ForgetPasswordScreen
import com.abhishek.truckerbuddy.ui.theme.TruckerBuddyTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPassActivity : ComponentActivity(),ForgetPassActivityCallBack {
    val forgetPassActivityCallBack=this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TruckerBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ForgetPasswordScreen(forgetPassActivityCallBack = forgetPassActivityCallBack)
                }
            }
        }
    }

    override fun sendLink(email:String) {
        val auth=Firebase.auth
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Password Reset Link Sent",
                            Toast.LENGTH_LONG,
                        ).show()
                        Log.d(TAG, "Email sent.")
                    }
                }

        val intent= Intent(this@ForgetPassActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}

@Composable
fun Greeting11(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview11() {
    TruckerBuddyTheme {
        Greeting11("Android")
    }
}