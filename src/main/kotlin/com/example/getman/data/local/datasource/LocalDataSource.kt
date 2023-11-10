package com.example.getman.data.local.datasource

import com.example.getman.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.logging.Logger

class LocalDataSource {
    private val logger = Logger.getLogger("LocalDataSource")

    suspend fun saveUserToFile(user: User) = withContext(Dispatchers.IO) {
        try {
            val userWithOutPassword = user.copy(password = null)
            val fileOut = FileOutputStream(FILE_NAME)
            val out = ObjectOutputStream(fileOut)
            out.writeObject(userWithOutPassword)
            out.close()
            fileOut.close()
            logger.info("Serialized data is saved in $FILE_NAME")
        } catch (i: IOException) {
            logger.severe("Save user failed")
        }
    }

    suspend fun readUserFromFile(): User? = withContext(Dispatchers.IO) {
        try {
            val fileIn = FileInputStream(FILE_NAME)
            val inStream = ObjectInputStream(fileIn)
            val user = inStream.readObject() as User
            inStream.close()
            fileIn.close()
            return@withContext user
        } catch (i: IOException) {
            logger.severe("Read user failed: ${i.message}")
        } catch (c: ClassNotFoundException) {
            logger.severe("Read user failed: ${c.message}")
        }
        return@withContext null
    }

    suspend fun removeUserFromFile() = withContext(Dispatchers.IO) {
        try {
            val file = File(FILE_NAME)
            file.writeText("")
            logger.info("Remove user from $FILE_NAME")
        } catch (e: IOException) {
            logger.severe("Remove user failed: ${e.message}")
        }
    }

    companion object {
        const val FILE_NAME = "user.txt"
    }
}