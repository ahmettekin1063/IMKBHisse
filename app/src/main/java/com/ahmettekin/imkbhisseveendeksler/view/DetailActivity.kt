package com.ahmettekin.imkbhisseveendeksler.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.model.DetailModel
import com.ahmettekin.imkbhisseveendeksler.model.DetailRequestModel
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiInterface
import com.ahmettekin.imkbhisseveendeksler.utils.AESEncryption
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailActivity : AppCompatActivity() {

    var aesKey:String?=null
    var aesIV:String?=null
    var id: String?=null
    var authorization: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initialization()
        getDetails()
    }

    private fun initialization() {
        id=intent.getStringExtra("id")
        authorization=intent.getStringExtra("authorization")
        aesKey=intent.getStringExtra("aesKey")
        aesIV=intent.getStringExtra("aesIV")
    }

    private fun getDetails() {
        val encryptedId= AESEncryption.encrypt("AES/CBC/PKCS7Padding",id,aesKey,aesIV)
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
                configureUI(response.body())
            }

            override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun configureUI(responseDetailData:DetailModel?){
        "Sembol: ${AESEncryption.decrypt("AES/CBC/PKCS7Padding",responseDetailData?.symbol,aesKey,aesIV)}".also { tvDetaySembol.text = it }
        "Fiyat: ${responseDetailData?.price.toString()}".also { tvDetayFiyat.text = it }
        "%Fark: ${kotlin.math.abs(responseDetailData?.difference!!)}".also { tvDetayFark.text = it }
        "Hacim: ${String.format("%.2f",responseDetailData.volume)}".also { tvDetayHacim.text = it }
        "Alış: ${responseDetailData.bid.toString()}".also { tvDetayAlis.text = it }
        "Satış:${responseDetailData.offer.toString()}".also { tvDetaySatis.text = it }
        "Günlük Düşük: ${responseDetailData.lowest.toString()}".also { tvDetayGunDus.text = it }
        "Günlük Yüksek: ${responseDetailData.highest.toString()}".also { tvDetayGunYuk.text = it }
        "Adet: ${responseDetailData.count.toString()}".also { tvDetayAdet.text = it }
        "Tavan: ${responseDetailData.maximum.toString()}".also { tvDetayTavan.text = it }
        "Taban: ${responseDetailData.minimum.toString()}".also { tvDetayTaban.text = it }

        if(responseDetailData.isUp!!){
            imgDetayDegisim.setImageResource(R.drawable.up_arrow)
        }else if(responseDetailData.isDown!!){
            imgDetayDegisim.setImageResource(R.drawable.down_arrow)
        }
    }
}