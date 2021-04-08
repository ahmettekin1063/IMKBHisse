package com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GraphicData(
    @SerializedName("day")
    var day: Int?,
    @SerializedName("value")
    var value: Double?
):Serializable
