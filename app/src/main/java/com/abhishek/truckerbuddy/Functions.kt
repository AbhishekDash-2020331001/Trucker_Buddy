package com.abhishek.truckerbuddy

import com.abhishek.truckerbuddy.composables.Truck
import java.time.LocalDate
import java.time.LocalTime

interface LoginCallBack{
    fun doSignIn(email:String,password:String)
    fun goToRegScreen()
    fun gotoForgetPass()
    fun sendLink(email: String)
}

interface SignUpCallBack{
    fun register(email: String, password: String,name:String,phone:String)
    fun goToLoginScreen()
}

interface PostCallBack{
    fun gotoFeed()
    fun gotoTruckScreen(ptime:LocalTime,pdate:LocalDate)
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
    fun placeYourBid(tripId:String,creator:String)
    fun gotoPostScreen()
    fun gotoProfileScreen()
}

interface MyBidsCallBack{
    fun acceptDeal(bidId: String,bidderId: String, tripId: String)
    fun rejectDeal(bidId: String,bidderId: String,creatorId: String)
}

interface ProfileCallBack{
    fun gotoPost()
    fun gotoFeed()
    fun myRunningTrips()
    fun sendVerificationMail()
    fun signOut()
    fun gotoReceivedBid()
    fun showToast(coin:Int)
}

interface MyRunningTripsCallBack{
    fun viewResponses(tripId: String)
}
interface ViewResponsesScreenCallBack{
  fun showBidderProfile(bidderId:String,bidId:String)
}

interface ForgetPassActivityCallBack{
    fun sendLink(email: String)
}

interface BidderProfileScreenActivityCallBack{
    fun sendDeal(bidId: String,bidderId: String,currentUserUid: String)
    fun cancelDeal(bidId: String,bidderId: String,currentUserUid : String)
}