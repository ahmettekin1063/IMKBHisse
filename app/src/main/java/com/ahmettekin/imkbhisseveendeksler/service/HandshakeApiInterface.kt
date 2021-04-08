package com.ahmettekin.imkbhisseveendeksler.service

import com.ahmettekin.imkbhisseveendeksler.model.HandshakeModel
import com.ahmettekin.imkbhisseveendeksler.model.HandshakeRequestModel
import retrofit2.Call
import retrofit2.http.*

interface HandshakeApiInterface {

    @Headers("Content-Type: application/json")
    @POST("api/handshake/start")
    fun getHandshake(@Body handshakeRequestModel: HandshakeRequestModel): Call<HandshakeModel>
}