package com.example.getman.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object PasswordEncryptor {
    fun encryptPassword(password: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hashedBytes = md.digest(password.toByteArray())
            hashedBytes.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}