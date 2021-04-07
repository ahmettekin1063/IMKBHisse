package com.ahmettekin.imkbhisseveendeksler

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle


import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.imkbhisseveendeksler.adapter.StocksAdapter
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import com.ahmettekin.imkbhisseveendeksler.model.ListModel.Stock
import com.ahmettekin.imkbhisseveendeksler.model.ListRequestModel
import com.ahmettekin.imkbhisseveendeksler.service.StocksApiInterface
import com.ahmettekin.imkbhisseveendeksler.utils.Utils
import kotlinx.android.synthetic.main.activity_stocks_and_indices.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class StocksAndIndices : AppCompatActivity() {
    private var myList: List<Stock?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks_and_indices)
        val aesKey = intent.getStringExtra("aesKey")
        val aesIV = intent.getStringExtra("aesIV")
        val authorization = intent.getStringExtra("authorization")
        val m=Utils.encrypt("AES/CBC/PKCS7Padding","all",aesKey,aesIV)
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

         val stocksApi=retrofit.create(StocksApiInterface::class.java)
         val apiCall = stocksApi.getStocks(ListRequestModel(m))

         apiCall.enqueue(object : Callback<ListModel> {
             override fun onResponse(call: Call<ListModel>, response: Response<ListModel>) {
                 recyclerView.layoutManager=LinearLayoutManager(this@StocksAndIndices)
                 recyclerView.adapter=StocksAdapter(response.body()?.stocks,aesKey!!,aesIV!!)
                 myList=response.body()?.stocks
             }
             override fun onFailure(call: Call<ListModel>, t: Throwable) {}
         })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val myFilteredList = ArrayList<Stock>()
                for (i in myList!!) {
                    if (Utils.decrypt("AES/CBC/PKCS7Padding",i!!.symbol,aesKey,aesIV)
                            .toLowerCase()
                            .trim()
                            .contains(newText!!.toLowerCase().trim())) {
                        myFilteredList.add(i)
                    }
                }
                recyclerView.layoutManager = LinearLayoutManager(this@StocksAndIndices)
                recyclerView.adapter = StocksAdapter(myFilteredList,aesKey!!,aesIV!!)
                return false
            }
        })

        setSupportActionBar(toolbar_stockList)
        val toggle= ActionBarDrawerToggle(this@StocksAndIndices,drawer,toolbar_stockList,0,0)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }
}