package com.ahmettekin.imkbhisseveendeksler

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle


import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.imkbhisseveendeksler.adapter.StocksAdapter
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import com.ahmettekin.imkbhisseveendeksler.model.ListModel.Stock
import com.ahmettekin.imkbhisseveendeksler.model.ListRequestModel
import com.ahmettekin.imkbhisseveendeksler.service.StocksApiInterface
import com.ahmettekin.imkbhisseveendeksler.utils.Utils
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_stocks_and_indices.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class StocksAndIndices : AppCompatActivity(){
    private var myList: List<Stock?>? = null
    private var aesKey:String?=null
    private var aesIV:String?=null
    private var authorization:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks_and_indices)
        aesKey = intent.getStringExtra("aesKey")
        aesIV = intent.getStringExtra("aesIV")
        authorization = intent.getStringExtra("authorization")
        val toggle= ActionBarDrawerToggle(this@StocksAndIndices,drawer,toolbar_stockList,0,0)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        configureRecylerView("all")

        navigationView.inflateHeaderView(R.layout.navigation_baslik)
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


        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_hisse -> configureRecylerView("all")
                R.id.item_yuk -> configureRecylerView("increasing")
                R.id.item_dus -> configureRecylerView("decreasing")
                R.id.item_hcm_30 -> configureRecylerView("volume30")
                R.id.item_hcm_50 -> configureRecylerView("volume50")
                R.id.item_hcm_100 -> configureRecylerView("volume100")
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun configureRecylerView(period: String) {
        val m=Utils.encrypt("AES/CBC/PKCS7Padding",period,aesKey,aesIV)
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

    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

}