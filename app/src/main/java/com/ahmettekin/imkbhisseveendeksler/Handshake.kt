package com.ahmettekin.imkbhisseveendeksler

import android.os.Build
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal val systemVersion = Build.VERSION.RELEASE
internal val platformName = "Android"
internal val deviceModel = Build.MODEL
internal val manufacturer = Build.MANUFACTURER
internal val deviceId = UUID.randomUUID().toString()

internal lateinit var aesKey: String
internal lateinit var aesIV: String
internal lateinit var authorization: String

internal val BASE_URL = "https://mobilechallenge.veripark.com/"




