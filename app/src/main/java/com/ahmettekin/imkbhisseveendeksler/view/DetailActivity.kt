package com.ahmettekin.imkbhisseveendeksler.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.model.DetailModel
import com.ahmettekin.imkbhisseveendeksler.model.DetailRequestModel
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiInterface
import com.ahmettekin.imkbhisseveendeksler.utils.Utils
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.abs

class DetailActivity : AppCompatActivity() {

    var aesKey:String?=null
    var aesIV:String?=null
    var id: String?=null
    var authorization: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        id=intent.getStringExtra("id")
        authorization=intent.getStringExtra("authorization")
        aesKey=intent.getStringExtra("aesKey")
        aesIV=intent.getStringExtra("aesIV")

        getDetails()
        println("$id****$authorization")
    }

    private fun getDetails() {

        val encryptedId= Utils.encrypt("AES/CBC/PKCS7Padding",id,aesKey,aesIV)

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("X-VP-Authorization", authorization!!)
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }

        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mobilechallenge.veripark.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val detailApi=retrofit.create(DetailApiInterface::class.java)
        val apiCall=detailApi?.getDetail(DetailRequestModel(encryptedId))

        apiCall?.enqueue(object : Callback<DetailModel>{
            override fun onResponse(call: Call<DetailModel>, response: Response<DetailModel>) {
                configUI(response.body())
            }

            override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun configUI(detailData:DetailModel?){

        "Sembol: ${Utils.decrypt("AES/CBC/PKCS7Padding",detailData?.symbol,aesKey,aesIV)}".also { tvDetaySembol.text = it }
        "Fiyat: ${detailData?.price.toString()}".also { tvDetayFiyat.text = it }
        "%Fark: ${kotlin.math.abs(detailData?.difference!!)}".also { tvDetayFark.text = it }
        "Hacim: ${String.format("%.2f",detailData.volume)}".also { tvDetayHacim.text = it }
        "Alış: ${detailData.bid.toString()}".also { tvDetayAlis.text = it }
        "Satış:${detailData.offer.toString()}".also { tvDetaySatis.text = it }
        "Günlük Düşük: ${detailData.lowest.toString()}".also { tvDetayGunDus.text = it }
        "Günlük Yüksek: ${detailData.highest.toString()}".also { tvDetayGunYuk.text = it }
        "Adet: ${detailData.count.toString()}".also { tvDetayAdet.text = it }
        "Tavan: ${detailData.maximum.toString()}".also { tvDetayTavan.text = it }
        "Taban: ${detailData.minimum.toString()}".also { tvDetayTaban.text = it }

        if(detailData.isUp!!){
            imgDetayDegisim.setImageResource(R.drawable.up_arrow)
        }else if(detailData.isDown!!){
            imgDetayDegisim.setImageResource(R.drawable.down_arrow)
        }
    }
}