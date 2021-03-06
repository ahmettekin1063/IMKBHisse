package com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage


import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DetailModel(
    @SerializedName("bid")
    var bid: Double?,
    @SerializedName("channge")
    var channge: Double?,
    @SerializedName("count")
    var count: Int?,
    @SerializedName("difference")
    var difference: Double?,
    @SerializedName("graphicData")
    var graphicData: List<GraphicData?>?,
    @SerializedName("highest")
    var highest: Double?,
    @SerializedName("isDown")
    var isDown: Boolean?,
    @SerializedName("isUp")
    var isUp: Boolean?,
    @SerializedName("lowest")
    var lowest: Double?,
    @SerializedName("maximum")
    var maximum: Double?,
    @SerializedName("minimum")
    var minimum: Double?,
    @SerializedName("offer")
    var offer: Double?,
    @SerializedName("price")
    var price: Double?,
    @SerializedName("status")
    var status: Status?,
    @SerializedName("symbol")
    var symbol: String?,
    @SerializedName("volume")
    var volume: Double?
) :Serializable