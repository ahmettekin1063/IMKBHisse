package com.ahmettekin.imkbhisseveendeksler.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
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
 *Created by Ahmet on 8.04.2021
 */
class MainActivity : AppCompatActivity() {
    private val systemVersion=Build.VERSION.RELEASE
    private val platformName="Android"
    private val deviceModel=Build.MODEL
    private val manufacturer =Build.MANUFACTURER

    var deviceId=""
    var mAesKey=""
    var mAesIV=""
    var mAuthorization=""

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceId=UUID.randomUUID().toString()
        getHandshake()
    }

    private fun getHandshake(){
        val requestModel = HandshakeRequestModel(deviceId,deviceModel,manufacturer,platformName,systemVersion)
        val handshakesApi=HandshakeApiClient.client?.create(HandshakeApiInterface::class.java)
        val apiCall=handshakesApi?.getHandshake(requestModel)
        button.visibility=View.INVISIBLE
        apiCall?.enqueue(object : Callback<HandshakeModel> {
            override fun onResponse(call: Call<HandshakeModel>, response: Response<HandshakeModel>) {
                if (response.isSuccessful && response.body()?.status?.isSuccess!!) {
                    mAesKey = response.body()?.aesKey!!
                    mAesIV = response.body()?.aesIV!!
                    mAuthorization = response.body()?.authorization!!
                    button.visibility = View.VISIBLE
                }else{
                    Toast.makeText(this@MainActivity,"Hata oluştu",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<HandshakeModel>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Hata: ${t.localizedMessage}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun goToStocksAndIndices(view: View){
        val intent= Intent(this@MainActivity, StocksAndIndicesActivity::class.java)
        intent.putExtra("aesKey",mAesKey)
        intent.putExtra("aesIV",mAesIV)
        intent.putExtra("authorization",mAuthorization)
        startActivity(intent)
    }
}