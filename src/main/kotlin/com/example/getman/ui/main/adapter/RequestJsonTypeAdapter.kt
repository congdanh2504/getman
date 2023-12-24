package com.example.getman.ui.main.adapter

import com.example.getman.ui.main.model.JsonRequest
import com.example.getman.ui.main.model.KeyValueTableModel
import com.example.getman.utils.BodyEnum
import com.example.getman.utils.RequestEnum
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class RequestJsonTypeAdapter : JsonSerializer<JsonRequest>, JsonDeserializer<JsonRequest> {
    override fun serialize(src: JsonRequest, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("requestType", src.requestType.toString())
        jsonObject.addProperty("url", src.url)
        jsonObject.add("params", context.serialize(src.params))
        jsonObject.add("headers", context.serialize(src.headers))
        jsonObject.addProperty("bodyType", src.bodyType.toString())
        jsonObject.add("formData", context.serialize(src.formData))
        jsonObject.addProperty("body", src.body)
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonRequest {
        val jsonObject = json.asJsonObject
        val requestType = RequestEnum.valueOf(jsonObject.get("requestType").asString)
        val url = jsonObject.get("url").asString
        val keyValueType = object : TypeToken<List<KeyValueTableModel>>() {}.type
        val params = context.deserialize<List<KeyValueTableModel>>(jsonObject.get("params"), keyValueType)
        val headers = context.deserialize<List<KeyValueTableModel>>(jsonObject.get("headers"), keyValueType)
        val bodyType = BodyEnum.valueOf(jsonObject.get("bodyType").asString)
        val formData = context.deserialize<List<KeyValueTableModel>>(jsonObject.get("formData"), keyValueType)
        val body = jsonObject.get("body")?.asString
        return JsonRequest(requestType, url, params, headers, bodyType, formData, body)
    }
}