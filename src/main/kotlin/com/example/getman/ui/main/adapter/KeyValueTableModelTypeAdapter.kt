package com.example.getman.ui.main.adapter

import com.example.getman.ui.main.model.KeyValueTableModel
import com.google.gson.*
import java.lang.reflect.Type

class KeyValueTableModelTypeAdapter : JsonSerializer<KeyValueTableModel>, JsonDeserializer<KeyValueTableModel> {
    override fun serialize(src: KeyValueTableModel, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("key", src.getKeyString())
        jsonObject.addProperty("value", src.getValueString())
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): KeyValueTableModel {
        val jsonObject = json.asJsonObject
        val key = jsonObject.get("key").asString
        val value = jsonObject.get("value").asString
        return KeyValueTableModel(key, value)
    }
}