package com.ahmettekin.imkbhisseveendeksler.service

import com.ahmettekin.imkbhisseveendeksler.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if(retrofit ==null){
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}