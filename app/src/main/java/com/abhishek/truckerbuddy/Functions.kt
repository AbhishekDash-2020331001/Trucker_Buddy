package com.abhishek.truckerbuddy

import android.content.Context
import androidx.compose.runtime.ProvidableCompositionLocal
import com.abhishek.truckerbuddy.composables.Truck

interface LoginCallBack{
    fun doSignIn(email:String,password:String)
    fun goToRegScreen()
}

interface SignUpCallBack{
    fun register(email: String, password: String,name:String,username:String)
    fun goToLoginScreen()
}

interface PostCallBack{
    fun gotoFeed()
    fun gotoTruckScreen()
    fun createPost(
        pickUpDate:String,
        pickUpTime:String,
        pickUpDivision:String,
        pickUpZilla:String,
        deliveryDivision:String,
        deliveryZilla:String,
        typeOfGood:String
    )
    fun gotoProfileScreen()
}

interface TruckScreenCallBack{
    fun gotoPostScreen(truck: Truck)
}

interface TripDetailScreenCallBack{
    fun placeBid(tripId:String,tripCreatorId:String,bidAmount:String)
}

interface FeedCallBack{
    fun placeYourBid(tripId:String)
    fun gotoPostScreen()
    fun gotoProfileScreen()
}

interface ProfileCallBack{
    fun gotoPost()
    fun gotoSettings()
    fun gotoFeed()
    fun myRunningTrips()
    fun sendVerificationMail()
}

interface MyRunningTripsCallBack{
    fun viewResponses(tripId: String)
}
interface ViewResponsesScreenCallBack{
  fun showBidderProfile(bidderId:String)
}

interface ForgetPassActivityCallBack{
    fun sendLink()
}