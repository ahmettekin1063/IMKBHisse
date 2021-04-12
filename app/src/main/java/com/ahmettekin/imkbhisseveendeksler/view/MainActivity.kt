package com.ahmettekin.imkbhisseveendeksler.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.ahmettekin.imkbhisseveendeksler.*
import com.ahmettekin.imkbhisseveendeksler.deviceModel
import com.ahmettekin.imkbhisseveendeksler.manufacturer
import com.ahmettekin.imkbhisseveendeksler.model.HandshakeModel
import com.ahmettekin.imkbhisseveendeksler.model.HandshakeRequestModel
import com.ahmettekin.imkbhisseveendeksler.platformName
import com.ahmettekin.imkbhisseveendeksler.service.ApiClient
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

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getHandshake()
    }

    private fun getHandshake(){
        val requestModel = HandshakeRequestModel(deviceId,deviceModel,manufacturer,platformName,systemVersion)
        val handshakesApi=ApiClient.client?.create(HandshakeApiInterface::class.java)
        val apiCall=handshakesApi?.getHandshake(requestModel)
        button.visibility=View.INVISIBLE
        apiCall?.enqueue(object : Callback<HandshakeModel> {
            override fun onResponse(call: Call<HandshakeModel>, response: Response<HandshakeModel>) {
                if (response.isSuccessful && response.body()?.status?.isSuccess!!) {
                    aesKey = response.body()?.aesKey!!
                    aesIV = response.body()?.aesIV!!
                    authorization = response.body()?.authorization!!
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
        startActivity(Intent(this@MainActivity, StocksAndIndicesActivity::class.java))
    }
}