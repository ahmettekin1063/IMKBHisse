package com.ahmettekin.imkbhisseveendeksler.service

import com.ahmettekin.imkbhisseveendeksler.model.DetailModel
import com.ahmettekin.imkbhisseveendeksler.model.DetailRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DetailApiInterface {

    @POST("api/stocks/detail")
    fun getDetail(@Body id: DetailRequestModel): Call<DetailModel>
}