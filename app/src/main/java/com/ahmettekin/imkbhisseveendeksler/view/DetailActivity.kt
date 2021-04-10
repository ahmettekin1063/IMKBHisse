package com.ahmettekin.imkbhisseveendeksler.view

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ahmettekin.imkbhisseveendeksler.R
import com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage.DetailModel
import com.ahmettekin.imkbhisseveendeksler.utils.AESEncryption
import com.ahmettekin.imkbhisseveendeksler.utils.MyMarkerView
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private var detailModel: DetailModel?=null
    private var aesKey:String?=null
    private var aesIV:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initialization()
        configureUI(detailModel)
        renderData()
    }

    private fun initialization() {
        detailModel=intent.getSerializableExtra("detailModel") as DetailModel?
        aesKey=intent.getStringExtra("aesKey")
        aesIV=intent.getStringExtra("aesIV")
        mChart.setTouchEnabled(true)
        mChart.setPinchZoom(false)
        val mv = MyMarkerView(applicationContext, R.layout.custom_marker_view)
        mv.chartView = mChart
        mChart.marker = mv
    }

    private fun configureUI(detailModel: DetailModel?) {
        "Sembol: ${AESEncryption.decrypt(detailModel?.symbol, aesKey, aesIV)}".also { tvDetaySembol.text = it }
        "Fiyat: ${detailModel?.price}".also { tvDetayFiyat.text = it }
        "%Fark: ${kotlin.math.abs(detailModel?.difference!!)}".also { tvDetayFark.text = it }
        "Hacim: ${String.format("%.2f", detailModel.volume)}".also { tvDetayHacim.text = it }
        "Alış: ${detailModel.bid}".also { tvDetayAlis.text = it }
        "Satış:${detailModel.offer}".also { tvDetaySatis.text = it }
        "Günlük Düşük: ${detailModel.lowest}".also { tvDetayGunDus.text = it }
        "Günlük Yüksek: ${detailModel.highest}".also { tvDetayGunYuk.text = it }
        "Adet: ${detailModel.count}".also { tvDetayAdet.text = it }
        "Tavan: ${detailModel.maximum}".also { tvDetayTavan.text = it }
        "Taban: ${detailModel.minimum}".also { tvDetayTaban.text = it }

        if (detailModel.isUp!!) {
            imgDetayDegisim.setImageResource(R.drawable.up_arrow)
        } else if (detailModel.isDown!!) {
            imgDetayDegisim.setImageResource(R.drawable.down_arrow)
        }
    }

    private fun renderData() {
        val values: ArrayList<Entry> = ArrayList()
        var maxStockValue=0f

        for(temp in detailModel?.graphicData!!){
            values.add(Entry(temp?.day!!.toFloat(),temp.value!!.toFloat()))
            if(temp.value!! >maxStockValue) maxStockValue= temp.value!!.toFloat()
        }

        val llXAxis = LimitLine(10f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        val xAxis: XAxis = mChart.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.axisMaximum = 30f
        xAxis.axisMinimum = 0f
        xAxis.setDrawLimitLinesBehindData(true)
        val ll1 = LimitLine(maxStockValue*1.2f, "Maximum Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        val ll2 = LimitLine(0f, "Minimum Limit")
        ll2.lineWidth = 4f
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f
        val leftAxis: YAxis = mChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.addLimitLine(ll1)
        leftAxis.addLimitLine(ll2)
        leftAxis.axisMaximum = maxStockValue*1.5f
        leftAxis.axisMinimum = 0f
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(false)
        leftAxis.setDrawLimitLinesBehindData(false)
        mChart.axisRight.isEnabled = false
        setData(values)
    }

    private fun setData(values: ArrayList<Entry>) {
        val set1: LineDataSet

        if (mChart.data != null && mChart.data.dataSetCount > 0) {
            set1 = mChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Sample Data");
            set1.setDrawIcons(false)
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.color = Color.RED
            set1.setCircleColor(Color.RED)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f);
            set1.formSize = 15f

            if (Utils.getSDKInt() >= 18) {
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.RED
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)
            val data = LineData(dataSets);
            mChart.data = data
        }
    }
}