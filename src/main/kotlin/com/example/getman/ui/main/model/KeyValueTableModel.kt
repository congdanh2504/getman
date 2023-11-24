package com.example.getman.ui.main.model

import javafx.beans.property.SimpleStringProperty

class KeyValueTableModel(key: String, value: String) {
    private val keyString: SimpleStringProperty
    private val valueString: SimpleStringProperty

    init {
        keyString = SimpleStringProperty(key)
        valueString = SimpleStringProperty(value)
    }

    fun getKeyString() = keyString.get()
    fun getValueString() = valueString.get()
}