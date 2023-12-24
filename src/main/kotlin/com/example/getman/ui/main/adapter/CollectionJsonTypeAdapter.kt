package com.example.getman.ui.main.adapter

import com.example.getman.ui.main.model.JsonCollection
import com.example.getman.ui.main.model.JsonRequest
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CollectionJsonTypeAdapter : JsonSerializer<JsonCollection>, JsonDeserializer<JsonCollection> {
    override fun serialize(src: JsonCollection, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", src.name)
        jsonObject.add("requests", context.serialize(src.requests))
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonCollection {
        val jsonObject = json.asJsonObject
        val name = jsonObject.get("name").asString
        val requestsType = object : TypeToken<List<JsonRequest>>() {}.type
        val requests = context.deserialize<List<JsonRequest>>(jsonObject.get("requests"), requestsType)
        return JsonCollection(name, requests)
    }
}