package com.ahmettekin.imkbhisseveendeksler

import android.os.Build
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private val algorithm = "AES/CBC/PKCS7Padding"

internal fun encrypt(input: String, key: String, iv: String): String {
    val chiper = Cipher.getInstance(algorithm)
    chiper.init(Cipher.ENCRYPT_MODE, strToSecretKey(key), strToIV(iv))
    val cipherText = chiper.doFinal(input.toByteArray())
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Base64.getEncoder().encodeToString(cipherText)
    } else {
        android.util.Base64.encodeToString(cipherText, android.util.Base64.DEFAULT)
    }
}

internal fun decrypt(cipherText: String, key: String, iv: String): String {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.DECRYPT_MODE, strToSecretKey(key), strToIV(iv))
    val plainText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        cipher.doFinal(Base64.getDecoder().decode(cipherText))
    } else {
        cipher.doFinal(android.util.Base64.decode(cipherText, android.util.Base64.DEFAULT))
    }
    return String(plainText)
}

private fun strToSecretKey(str: String): SecretKey {
    val decodedKey = str.strDecryption()
    return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
}

private fun strToIV(str: String): IvParameterSpec {
    val iv = str.strDecryption()
    return IvParameterSpec(iv)
}


private fun String.strDecryption():ByteArray{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Base64.getDecoder().decode(this)
    } else {
        android.util.Base64.decode(this, android.util.Base64.DEFAULT)
    }
}