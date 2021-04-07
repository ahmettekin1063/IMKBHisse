package com.ahmettekin.imkbhisseveendeksler.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.model.DetailModel
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiClient
import com.ahmettekin.imkbhisseveendeksler.service.DetailApiInterface
import com.ahmettekin.imkbhisseveendeksler.utils.Utils
import com.ahmettekin.imkbhisseveendeksler.view.DetailActivity
import kotlinx.android.synthetic.main.row_layout.view.*
import retrofit2.Call
import retrofit2.Response
import java.text.DecimalFormat
import javax.security.auth.callback.Callback
import kotlin.math.abs

class StocksAdapter(private val myList: List<ListModel.Stock?>?,val aesKey:String, val aesIV:String):RecyclerView.Adapter<StocksAdapter.MyViewHolder>() {


    class MyViewHolder(myItemView: View) : RecyclerView.ViewHolder(myItemView){
        var mitemView:View

        init {
            this.mitemView=myItemView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_layout,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mitemView.tvSembol.text= Utils.decrypt("AES/CBC/PKCS7Padding",myList?.get(position)?.symbol,aesKey,aesIV)
        holder.mitemView.tvFiyat.text= DecimalFormat("##.##").format(myList?.get(position)?.price)
        holder.mitemView.tvFark.text= DecimalFormat("##.##").format(abs(myList?.get(position)?.difference!!))
        holder.mitemView.tvAlis.text= DecimalFormat("##.##").format(myList[position]?.bid)
        holder.mitemView.tvSatis.text= DecimalFormat("##.##").format(myList[position]?.offer)
        holder.mitemView.tvHacim.text= DecimalFormat("##.##").format(myList[position]?.volume)

        if(myList[position]?.isUp!!){
            holder.mitemView.imgDegisim.setImageResource(R.drawable.up_arrow)
        }else if(myList[position]?.isDown!!){
            holder.mitemView.imgDegisim.setImageResource(R.drawable.down_arrow)
        }

        if(position%2==0){
            holder.mitemView.setBackgroundColor(holder.mitemView.context.resources.getColor(R.color.white))
        }else{
            holder.mitemView.setBackgroundColor(holder.mitemView.context.resources.getColor(R.color.grey))
        }

        holder.mitemView.setOnClickListener {
            val id="bilinmiyor"
            val intent= Intent(it.context,DetailActivity::class.java)
            intent.putExtra("id",id)
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return myList?.size!!
    }
}