package com.ahmettekin.imkbhisseveendeksler.service

import com.ahmettekin.imkbhisseveendeksler.model.DetailModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DetailApiInterface {

    @GET("api/stocks/detail")
    fun  getDetail(@Query("id") id:String): Call<DetailModel>
}