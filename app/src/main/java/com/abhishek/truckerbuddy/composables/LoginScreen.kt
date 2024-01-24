package com.abhishek.truckerbuddy.composables

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhishek.truckerbuddy.LoginCallBack
import com.abhishek.truckerbuddy.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(loginCallBack: LoginCallBack){
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    var isChecked by remember{ mutableStateOf(false) }
    val (focusUsername,focusPassword) = remember { FocusRequester.createRefs()}
    val keyboardController =  LocalSoftwareKeyboardController.current
    var isPasswordVisible by remember{ mutableStateOf(false) }

        Column(
            modifier=Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Image(
                painter = painterResource(id = R.drawable.truckl),
                contentDescription = null,
                modifier = Modifier.wrapContentSize()
            )
            Text(
                text = "TRUCKER BUDDY",
                color = Color.Black,
                //modifier = Modifier.padding(start=100.dp),
                fontSize = 22.sp
            )
            Card(
                modifier=Modifier
                    .padding(8.dp)
                    .wrapContentSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(10.dp)){
                Column(modifier=Modifier.wrapContentSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier=Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .focusRequester(focusUsername),
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, null) },
                        label = { Text(text = "Email") },
                        shape = CutCornerShape(10.dp),

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black, // Set the text color to black
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {focusPassword.requestFocus()}),
                        visualTransformation = VisualTransformation.None
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .focusRequester(focusPassword),
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, null) },
                        label = { Text(text = "Password") },
                        shape = CutCornerShape(10.dp),

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black, // Set the text color to black
                            cursorColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions (onDone = {keyboardController?.hide()}),
                        visualTransformation = if(isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {isPasswordVisible = !isPasswordVisible}) {
                                Icon(imageVector = Icons.Default.RemoveRedEye,
                                    contentDescription ="Password Toggle" )

                            }
                        }
                    )
                    Row(modifier=Modifier.fillMaxWidth(),Arrangement.End) {
                        TextButton(onClick = { /*TODO*/ }) {
                            Text(
                                text = "Forgot Password?",
                                textAlign = TextAlign.End,
                                style = TextStyle(
                                    fontSize = 13
                                        .sp,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Normal,
                                    letterSpacing = 2.sp,
                                )
                            )
                        }
                    }
                    
                    Button(onClick = {
                                     loginCallBack.doSignIn(email,password)
                    }, shape = CutCornerShape(10.dp)
                        ) {
                        Text(text = "LOGIN",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Normal,
                                letterSpacing = 2.sp,
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        Arrangement.Center,verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Don't have account?",fontSize = 14.sp)
                        TextButton(onClick = {
                            loginCallBack.goToRegScreen()
                        }) {
                            Text(text = "Sign Up Now",
                                style = TextStyle(
                                    fontSize = 15
                                        .sp,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Normal,
                                    letterSpacing = 2.sp,
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                }

            }

        }




}



@Composable
fun CustomRoundedCornerShape(
    topLeft: Float,
    topRight: Float,
    bottomRight: Float,
    bottomLeft: Float
) = RoundedCornerShape(
    topStart = topLeft.dp,
    topEnd = topRight.dp,
    bottomEnd = bottomRight.dp,
    bottomStart = bottomLeft.dp
)