package com.example.getman.ui.main.adapter

import com.example.getman.GetManApplication
import com.example.getman.ui.main.model.CollectionType
import com.example.getman.ui.main.model.RequestModel
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TreeCell

class CollectionItemTreeCell(
    private val onCollectionDelete: (CollectionType.CollectionTitle) -> Unit,
    private val onRequestDelete: (CollectionType.CollectionRequest) -> Unit,
    private val onRequestClick: (RequestModel) -> Unit,
    private val onAdd: (CollectionType.CollectionTitle) -> Unit
) : TreeCell<CollectionType>() {

    override fun updateItem(item: CollectionType?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty) {
            text = null
            graphic = null
        } else {
            item?.let { collectionItem ->
                val fxmlLoader: FXMLLoader = GetManApplication.instance.loadFxml("collection-item.fxml")
                val node = fxmlLoader.load<Node>()
                val requestTypeLabel = node.lookup("#lblRequestType") as Label
                val urlLabel = node.lookup("#lblUrl") as Label
                val deleteButton = node.lookup("#btnDelete") as Button
                val addButton = node.lookup("#btnAdd") as Button
                if (collectionItem is CollectionType.CollectionTitle) {
                    addButton.isVisible = true
                    addButton.setOnAction {
                        onAdd.invoke(collectionItem)
                    }
                    requestTypeLabel.text = collectionItem.name
                    deleteButton.setOnAction {
                        onCollectionDelete.invoke(collectionItem)
                    }
                } else if (collectionItem is CollectionType.CollectionRequest) {
                    addButton.isVisible = false
                    requestTypeLabel.text = collectionItem.request.requestType.value
                    urlLabel.text = collectionItem.request.url
                    urlLabel.setOnMousePressed {
                        onRequestClick.invoke(collectionItem.request)
                    }
                    deleteButton.setOnAction {
                        onRequestDelete.invoke(collectionItem)
                    }
                }
                graphic = node
            }

        }
    }
}