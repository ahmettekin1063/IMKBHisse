package com.ahmettekin.imkbhisseveendeksler.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.model.HandshakeModel
import com.ahmettekin.imkbhisseveendeksler.model.HandshakeRequestModel
import com.ahmettekin.imkbhisseveendeksler.service.HandshakeApiClient
import com.ahmettekin.imkbhisseveendeksler.service.HandshakeApiInterface
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 *Created by Ahmet Tekin on 8.04.2021
 */
class MainActivity : AppCompatActivity() {
    val systemVersion="11"
    val platformName="AndroidSimulator"
    val deviceModel="sdk_gphone_x86"
    val manifacturer="Google"

    var mAesKey=""
    var mAesIV=""
    var mAuthorization=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val deviceId=UUID.randomUUID().toString()
        val requestModel = HandshakeRequestModel(deviceId,deviceModel,manifacturer,platformName,systemVersion)
        val handshakesApi=HandshakeApiClient.client?.create(HandshakeApiInterface::class.java)
        val apiCall=handshakesApi?.getHandshake(requestModel)
        button.visibility=View.INVISIBLE
        apiCall?.enqueue(object : Callback<HandshakeModel> {
            override fun onResponse(call: Call<HandshakeModel>, response: Response<HandshakeModel>) {
                mAesKey=response.body()?.aesKey!!
                mAesIV=response.body()?.aesIV!!
                mAuthorization=response.body()?.authorization!!
                button.visibility=View.VISIBLE
            }
            override fun onFailure(call: Call<HandshakeModel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun goToStocksAndIndices(view: View){
        val intent= Intent(this@MainActivity, StocksAndIndices::class.java)
        intent.putExtra("aesKey",mAesKey)
        intent.putExtra("aesIV",mAesIV)
        intent.putExtra("authorization",mAuthorization)
        startActivity(intent)
    }
}