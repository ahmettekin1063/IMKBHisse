package com.ahmettekin.imkbhisseveendeksler.service

import com.ahmettekin.imkbhisseveendeksler.authorization
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import com.ahmettekin.imkbhisseveendeksler.model.ListRequestModel
import retrofit2.Call
import retrofit2.http.*

interface StocksApiInterface {

    @Headers("Content-Type: application/json")
    @POST("api/stocks/list")
    fun getStocks(@Body period: ListRequestModel, @Header("X-VP-Authorization") authorization: String): Call<ListModel>
}