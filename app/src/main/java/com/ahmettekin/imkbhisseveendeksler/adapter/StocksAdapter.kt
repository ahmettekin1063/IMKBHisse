package com.ahmettekin.imkbhisseveendeksler.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage.DetailModel
import com.ahmettekin.imkbhisseveendeksler.model.DetailRequestModel
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiInterface
import com.ahmettekin.imkbhisseveendeksler.utils.AESEncryption
import com.ahmettekin.imkbhisseveendeksler.view.DetailActivity
import kotlinx.android.synthetic.main.row_layout.view.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class StocksAdapter(private val myList: List<ListModel.Stock?>?, private val aesKey: String, private val aesIV: String, private val authorization: String
) : RecyclerView.Adapter<StocksAdapter.MyViewHolder>() {

    private val BASE_URL = "https://mobilechallenge.veripark.com/"

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        configureUI(holder, position)
    }

    private fun configureUI(holder: MyViewHolder, position: Int) {
        holder.itemView.tvDegisim.visibility=View.INVISIBLE
        holder.itemView.tvSembol.text = AESEncryption.decrypt(myList?.get(position)?.symbol, aesKey, aesIV)
        holder.itemView.tvFiyat.text = String.format("%.2f", myList?.get(position)?.price)
        holder.itemView.tvFark.text = String.format("%.2f", abs(myList?.get(position)?.difference!!))
        holder.itemView.tvAlis.text = String.format("%.2f", myList[position]?.bid)
        holder.itemView.tvSatis.text = String.format("%.2f", myList[position]?.offer)
        holder.itemView.tvHacim.text = String.format("%.2f", myList[position]?.volume)

        when {
            myList[position]?.isUp!! -> holder.itemView.imgDegisim.setImageResource(R.drawable.up_arrow)
            myList[position]?.isDown!! -> holder.itemView.imgDegisim.setImageResource(R.drawable.down_arrow)
            else ->  holder.itemView.imgDegisim.setImageResource(R.drawable.none)
        }

        if (position % 2 == 0) holder.itemView.setBackgroundColor(
            holder.itemView.context.resources.getColor(
                R.color.white
            )
        )
        else holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.grey))

        holder.itemView.setOnClickListener {
            val id = myList[position]?.id.toString()
            postDetailsToDetailActivity(id, aesKey, aesIV, it.context)
        }
    }

    override fun getItemCount() = myList?.size!!

    private fun postDetailsToDetailActivity(id: String, aesKey: String, aesIV: String, context: Context) {
        val encryptedId = AESEncryption.encrypt(id, aesKey, aesIV)
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("X-VP-Authorization", authorization)
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

        val detailApi = retrofit.create(DetailApiInterface::class.java)
        val apiCall = detailApi?.getDetail(DetailRequestModel(encryptedId))

        apiCall?.enqueue(object : Callback<DetailModel> {
            override fun onResponse(call: Call<DetailModel>, response: Response<DetailModel>) {
                if (response.isSuccessful&&response.body()?.status?.isSuccess!!) {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("detailModel", response.body() as DetailModel)
                    intent.putExtra("aesKey", aesKey)
                    intent.putExtra("aesIV", aesIV)
                    context.startActivity(intent)
                }else{
                    Toast.makeText(context,"Hata",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                Toast.makeText(context, "Hata: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}