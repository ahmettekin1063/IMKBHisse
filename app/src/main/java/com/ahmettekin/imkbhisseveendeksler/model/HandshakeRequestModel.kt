package com.ahmettekin.imkbhisseveendeksler.model


import com.google.gson.annotations.SerializedName

class HandshakeRequestModel(
    @SerializedName("deviceId")
    var deviceId: String?,
    @SerializedName("deviceModel")
    var deviceModel: String?,
    @SerializedName("manifacturer")
    var manifacturer: String?,
    @SerializedName("platformName")
    var platformName: String?,
    @SerializedName("systemVersion")
    var systemVersion: String?
)