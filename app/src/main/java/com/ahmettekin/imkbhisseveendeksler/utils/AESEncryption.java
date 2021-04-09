package com.ahmettekin.imkbhisseveendeksler.utils;

import android.os.Build;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption {

    private final static String algorithm="AES/CBC/PKCS7Padding";

    public static String encrypt(String input, String key, String iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, strToSecretKey(key), strToIV(iv));
        byte[] cipherText = cipher.doFinal(input.getBytes());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(cipherText);
        }
        else{
            return android.util.Base64.encodeToString(cipherText,android.util.Base64.DEFAULT);
        }
    }

    public static String decrypt(String cipherText, String key, String iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, strToSecretKey(key), strToIV(iv));
        byte[] plainText;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        }
        else{
            plainText = cipher.doFinal(android.util.Base64.decode(cipherText,android.util.Base64.DEFAULT));
        }
        return new String(plainText);
    }

    private static SecretKey strToSecretKey(String str){
        byte[] decodedKey;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decodedKey = Base64.getDecoder().decode(str);
        }
        else{
            decodedKey = android.util.Base64.decode(str,android.util.Base64.DEFAULT);
        }

        return new SecretKeySpec(decodedKey,0,decodedKey.length,"AES");
    }

    private static IvParameterSpec strToIV(String str){
        byte[] iv;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            iv = Base64.getDecoder().decode(str);
        }
        else{
            iv = android.util.Base64.decode(str,android.util.Base64.DEFAULT);
        }

        return new IvParameterSpec(iv);
    }
}
