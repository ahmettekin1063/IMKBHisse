package com.ahmettekin.imkbhisseveendeksler.model


import com.google.gson.annotations.SerializedName

class ListModel(
    @SerializedName("status")
    var status: Status?,
    @SerializedName("stocks")
    var stocks: List<Stock?>?
) {
    class Status(
        @SerializedName("error")
        var error: Error?,
        @SerializedName("isSuccess")
        var isSuccess: Boolean?
    ) {
        class Error(
            @SerializedName("code")
            var code: Int?,
            @SerializedName("message")
            var message: String?
        )
    }

    class Stock(
        @SerializedName("bid")
        var bid: Double?,
        @SerializedName("difference")
        var difference: Double?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("isDown")
        var isDown: Boolean?,
        @SerializedName("isUp")
        var isUp: Boolean?,
        @SerializedName("offer")
        var offer: Double?,
        @SerializedName("price")
        var price: Double?,
        @SerializedName("symbol")
        var symbol: String?,
        @SerializedName("volume")
        var volume: Double?
    )
}