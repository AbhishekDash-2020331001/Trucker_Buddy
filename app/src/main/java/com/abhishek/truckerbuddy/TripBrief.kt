package com.abhishek.truckerbuddy

data class TripBrief(
    val pickUpTime:String,
    val tripId:String,
    val pickUpDate: String,
    val pickUpLocation: String,
    val deliveryLocation: String,
    val truckType: String,
    val truckCapacity: Int,
    val goodsType: String,
    val assigned: String,
    val ongoing: Boolean,
    val rated: Boolean,
    val creator: String
)
