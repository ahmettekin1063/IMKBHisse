package com.ahmettekin.imkbhisseveendeksler.view

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle


import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.adapter.StocksAdapter
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import com.ahmettekin.imkbhisseveendeksler.model.ListModel.Stock
import com.ahmettekin.imkbhisseveendeksler.model.ListRequestModel
import com.ahmettekin.imkbhisseveendeksler.service.StocksApiInterface
import com.ahmettekin.imkbhisseveendeksler.utils.AESEncryption
import kotlinx.android.synthetic.main.activity_stocks_and_indices.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class StocksAndIndicesActivity : AppCompatActivity(){
    private var myList: List<Stock?>? = null
    private var aesKey:String?=null
    private var aesIV:String?=null
    private var authorization:String?=null
    private val BASE_URL="https://mobilechallenge.veripark.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks_and_indices)
        initialization()
        configureListener()
    }

    private fun initialization() {
        aesKey = intent.getStringExtra("aesKey")
        aesIV = intent.getStringExtra("aesIV")
        authorization = intent.getStringExtra("authorization")
        val toggle= ActionBarDrawerToggle(this@StocksAndIndicesActivity,drawer,toolbar_stockList,0,0)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        configureRecylerView("all")
        navigationView.inflateHeaderView(R.layout.navigation_baslik)
    }

    private fun configureRecylerView(period: String) {
        val encryptedPeriod=
            AESEncryption.encrypt(period,aesKey,aesIV)
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
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val stocksApi=retrofit.create(StocksApiInterface::class.java)
        val apiCall = stocksApi.getStocks(ListRequestModel(encryptedPeriod))

        apiCall.enqueue(object : Callback<ListModel> {
            override fun onResponse(call: Call<ListModel>, response: Response<ListModel>) {
                if (response.isSuccessful&&response.body()?.status?.isSuccess!!) {
                    recyclerView.layoutManager = LinearLayoutManager(this@StocksAndIndicesActivity)
                    recyclerView.adapter =
                        StocksAdapter(response.body()?.stocks, aesKey!!, aesIV!!, authorization!!)
                    myList = response.body()?.stocks
                }else{
                    Toast.makeText(this@StocksAndIndicesActivity,"Hata oluştu",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ListModel>, t: Throwable) {
                Toast.makeText(this@StocksAndIndicesActivity,"Hata: ${t.localizedMessage}",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configureListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val myFilteredList = ArrayList<Stock>()
                for (temp in myList!!) {
                    if (AESEncryption.decrypt(temp!!.symbol,aesKey,aesIV)
                            .toLowerCase()
                            .trim()
                            .contains(newText!!.toLowerCase().trim())) {
                        myFilteredList.add(temp)
                    }
                }
                recyclerView.layoutManager = LinearLayoutManager(this@StocksAndIndicesActivity)
                recyclerView.adapter = StocksAdapter(myFilteredList,aesKey!!,aesIV!!,authorization!!)
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

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }
}