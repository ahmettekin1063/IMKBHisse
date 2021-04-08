package com.ahmettekin.imkbhisseveendeksler.service

import com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage.DetailModel
import com.ahmettekin.imkbhisseveendeksler.model.DetailRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface DetailApiInterface {

    @POST("api/stocks/detail")
    fun getDetail(@Body id: DetailRequestModel): Call<DetailModel>
}