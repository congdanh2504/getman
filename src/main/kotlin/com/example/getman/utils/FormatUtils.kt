package com.example.getman.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object FormatUtils {

    fun formatHtml(html: String): String {
        val document: Document = Jsoup.parse(html)
        document.outputSettings().indentAmount(4).prettyPrint(true)
        return document.html()
    }

    fun formatJson(json: String): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonElement = JsonParser.parseString(json)
        return gson.toJson(jsonElement)
    }
}