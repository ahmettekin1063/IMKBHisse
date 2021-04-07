package com.ahmettekin.imkbhisseveendeksler.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.model.DetailModel
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiClient
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiInterface
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val id=intent.getStringExtra("id")
        val detailApi=DetailApiClient.client?.create(DetailApiInterface::class.java)
        val apiCall=detailApi?.getDetail(id!!)

        apiCall?.enqueue(object : Callback<DetailModel>{
            override fun onResponse(call: Call<DetailModel>, response: Response<DetailModel>) {

                tvDetaySembol.text=response.body()?.symbol
                tvDetayFiyat.text=response.body()?.price.toString()
                tvDetayFark.text= response.body()?.difference.toString()
                tvDetayHacim.text=response.body()?.volume.toString()
                tvDetayAlis.text=response.body()?.bid.toString()
                tvDetaySatis.text=response.body()?.offer.toString()
                tvDetayGunDus.text=response.body()?.lowest.toString()
                tvDetayGunYuk.text=response.body()?.highest.toString()
                tvDetayAdet.text=response.body()?.count.toString()
                tvDetayTavan.text=response.body()?.maximum.toString()
                tvDetayTaban.text=response.body()?.minimum.toString()

                if(response.body()?.isUp!!){
                    imgDetayDegisim.setImageResource(R.drawable.up_arrow)
                }else if(response.body()?.isDown!!){
                    imgDetayDegisim.setImageResource(R.drawable.down_arrow)
                }


            }

            override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }
}