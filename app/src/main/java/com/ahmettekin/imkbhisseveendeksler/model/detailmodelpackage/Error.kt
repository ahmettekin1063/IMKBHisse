package com.ahmettekin.imkbhisseveendeksler.model.detailmodelpackage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Error(
    @SerializedName("code")
    var code: Int?,
    @SerializedName("message")
    var message: String?
):Serializable