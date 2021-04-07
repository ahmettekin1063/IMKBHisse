package com.ahmettekin.imkbhisseveendeksler.model


import com.google.gson.annotations.SerializedName

class HandshakeModel(
    @SerializedName("aesIV")
    var aesIV: String?,
    @SerializedName("aesKey")
    var aesKey: String?,
    @SerializedName("authorization")
    var authorization: String?,
    @SerializedName("lifeTime")
    var lifeTime: String?,
    @SerializedName("status")
    var status: Status?
) {
    class Status(
        @SerializedName("error")
        var error: Error?,
        @SerializedName("isSuccess")
        var isSuccess: Boolean?
    ) {
        class Error(
            @SerializedName("code")
            var code: Int?,
            @SerializedName("message")
            var message: String?
        )
    }
}