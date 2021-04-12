package com.ahmettekin.imkbhisseveendeksler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.decrypt
import com.ahmettekin.imkbhisseveendeksler.listener.RecyclerViewOnClickListener
import com.ahmettekin.imkbhisseveendeksler.model.ListModel
import kotlinx.android.synthetic.main.row_layout.view.*
import kotlin.math.abs

class StocksAdapter(private val myList: List<ListModel.Stock?>?, private val aesKey: String, private val aesIV: String, private val authorization: String
                    , private val listener: RecyclerViewOnClickListener
) : RecyclerView.Adapter<StocksAdapter.MyViewHolder>() {

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
        holder.itemView.tvSembol.text = decrypt(myList?.get(position)?.symbol!!, aesKey, aesIV)
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
            myList[position]?.id?.let {
                listener.recyclerViewItemClick(it)
            }
        }
    }

    override fun getItemCount() = myList?.size!!

}