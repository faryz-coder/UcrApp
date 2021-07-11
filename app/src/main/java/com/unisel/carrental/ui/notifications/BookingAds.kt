package com.unisel.carrental.ui.notifications

data class BookingAds (
    val title: String,
    val price: String,
    val total: String,
    val image: String,
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String,
    val clientId: String,
    val status: String,
    val type: String,
    val ownerId: String,
    val adsId: String,
    val adsOwnerId: String
)
