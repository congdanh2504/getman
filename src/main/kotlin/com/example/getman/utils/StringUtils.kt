package com.example.getman.utils

object StringUtils {

    fun insertNextLine(text: String, cycle: Int = 45): String {
        val result = StringBuilder()
        text.forEachIndexed { index, c ->
            if (index != 0 && index % cycle == 0) {
                result.append('\n')
            }
            result.append(c)
        }
        return result.toString()
    }
}