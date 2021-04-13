package com.ahmettekin.imkbhisseveendeksler.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmettekin.imkbhisseveendeksler.*
import com.ahmettekin.imkbhisseveendeksler.adapter.StocksAdapter
import com.ahmettekin.imkbhisseveendeksler.aesIV
import com.ahmettekin.imkbhisseveendeksler.aesKey
import com.ahmettekin.imkbhisseveendeksler.authorization
import com.ahmettekin.imkbhisseveendeksler.listener.RecyclerViewOnClickListener
import com.ahmettekin.imkbhisseveendeksler.model.DetailRequestModel
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import com.ahmettekin.imkbhisseveendeksler.model.ListModel.Stock
import com.ahmettekin.imkbhisseveendeksler.model.ListRequestModel
import com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage.DetailModel
import com.ahmettekin.imkbhisseveendeksler.service.ApiClient
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiInterface
import com.ahmettekin.imkbhisseveendeksler.service.StocksApiInterface
import kotlinx.android.synthetic.main.activity_detail.view.*
import kotlinx.android.synthetic.main.activity_stocks_and_indices.*
import kotlinx.android.synthetic.main.row_layout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class StocksAndIndicesActivity : AppCompatActivity(), RecyclerViewOnClickListener {
    private var myList: List<Stock?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks_and_indices)
        initialization()
        configureListener()
  }

    private fun initialization() {
        val toggle = ActionBarDrawerToggle(this@StocksAndIndicesActivity, drawer, toolbar_stockList, 0, 0)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        configureRecylerView(Periods.ALL.period)
        navigationView.inflateHeaderView(R.layout.navigation_baslik)
    }

    private fun configureRecylerView(period: String) {
        val encryptedPeriod =encrypt(period, aesKey, aesIV)
        val stocksApi = ApiClient.client?.create(StocksApiInterface::class.java)
        val apiCall = stocksApi?.getStocks(ListRequestModel(encryptedPeriod), authorization)

        apiCall?.enqueue(object : Callback<ListModel> {
            override fun onResponse(call: Call<ListModel>, response: Response<ListModel>) {
                if (response.isSuccessful && response.body()?.status?.isSuccess!!) {
                    recyclerView.layoutManager = LinearLayoutManager(this@StocksAndIndicesActivity)
                    recyclerView.adapter =
                        StocksAdapter(response.body()?.stocks, aesKey, aesIV, authorization,this@StocksAndIndicesActivity)
                    myList = response.body()?.stocks
                } else Toast.makeText(
                    this@StocksAndIndicesActivity,
                    "Hata oluştu",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<ListModel>, t: Throwable) {
                Toast.makeText(
                    this@StocksAndIndicesActivity,
                    "Hata: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun configureListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val myFilteredList = ArrayList<Stock>()
                /**
                 * IMPORTANT!!!apply ' a bak unwrapping
                 */
                myList?.let {
                    for (temp in it) {
                        if (decrypt(temp!!.symbol!!, aesKey, aesIV).toLowerCase().trim()
                                .contains(newText!!.toLowerCase().trim())
                        ) {
                            myFilteredList.add(temp)
                        }
                    }
                }

                recyclerView.layoutManager = LinearLayoutManager(this@StocksAndIndicesActivity)
                recyclerView.adapter = StocksAdapter(myFilteredList, aesKey, aesIV, authorization,this@StocksAndIndicesActivity)
                return true
            }
        })

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_hisse -> configureRecylerView(Periods.ALL.period)
                R.id.item_yuk -> configureRecylerView(Periods.INCREASING.period)
                R.id.item_dus -> configureRecylerView(Periods.DECREASING.period)
                R.id.item_hcm_30 -> configureRecylerView(Periods.VOLUME_30.period)
                R.id.item_hcm_50 -> configureRecylerView(Periods.VOLUME_50.period)
                R.id.item_hcm_100 -> configureRecylerView(Periods.VOLUME_100.period)
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }

        drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (slideOffset != 0f && slideOffset > 0.35f) this@StocksAndIndicesActivity.window.statusBarColor =
                    resources.getColor(R.color.grey)
                else this@StocksAndIndicesActivity.window.statusBarColor =
                    resources.getColor(R.color.red)
            }

            override fun onDrawerOpened(drawerView: View) {
                searchView.clearFocus()
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}

        })
    }

    override fun recyclerViewItemClick(id:Int) {
        //postDetailsToDetailActivity(id)
        val intent=Intent(this@StocksAndIndicesActivity, DetailActivity::class.java)
        intent.putExtra("id",id)
        startActivity(intent)
    }

    /*private fun postDetailsToDetailActivity(id: Int) {
        val encryptedId = encrypt(id.toString(), aesKey, aesIV)
        val detailApi = ApiClient.client?.create(DetailApiInterface::class.java)
        val apiCall = detailApi?.getDetail(DetailRequestModel(encryptedId),authorization)

        apiCall?.enqueue(object : Callback<DetailModel> {
            override fun onResponse(call: Call<DetailModel>, response: Response<DetailModel>) {
                if (response.isSuccessful&&response.body()?.status?.isSuccess!!) {
                    val intent = Intent(this@StocksAndIndicesActivity, DetailActivity::class.java)
                    intent.putExtra("detailModel", response.body() as DetailModel)
                    startActivity(intent)
                }else{
                    Toast.makeText(this@StocksAndIndicesActivity,"Hata",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                Toast.makeText(this@StocksAndIndicesActivity, "Hata: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }*/

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}