package com.ahmettekin.imkbhisseveendeksler.service

import com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage.DetailModel
import com.ahmettekin.imkbhisseveendeksler.model.DetailRequestModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface DetailApiInterface {

    @Headers("Content-Type: application/json")
    @POST("api/stocks/detail")
    fun getDetail(@Body id: DetailRequestModel,@Header("X-VP-Authorization") authorization: String): Observable<DetailModel>
}