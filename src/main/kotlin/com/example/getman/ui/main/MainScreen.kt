package com.example.getman.ui.main

import com.example.getman.GetManApplication
import com.example.getman.base.Screen
import javafx.collections.ObservableMap
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.HBox
import org.koin.core.inject

class MainScreen : Screen() {

    lateinit var addButton: Tab
    lateinit var tabPane: TabPane
    override val viewModel by inject<MainViewModel>()
    private var tabCount = 0

    override fun onCreate() {
        super.onCreate()
        createNewTab()
        tabPane.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            if (newTab == addButton) {
                createNewTab()
            }
        }
    }

    private fun createNewTab() {
        val fxmlLoader = GetManApplication.instance.loadFxml("new-tab.fxml")
        val newTab = fxmlLoader.load<Tab>()
        tabPane.tabs.add(tabCount++, newTab)
        tabPane.selectionModel.select(newTab)

        val namespace: ObservableMap<String, Any> = fxmlLoader.namespace
        val closeLabel: Label = namespace["closeLabel"] as Label
        closeLabel.setOnMouseClicked {
            if (tabCount == 1) return@setOnMouseClicked
            tabPane.tabs.remove(newTab)
            --tabCount
        }
    }

    companion object {
        const val FXML_VIEW_NAME = "main-page.fxml"
    }
}