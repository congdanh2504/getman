package com.example.getman.ui.main.adapter

import com.example.getman.GetManApplication
import com.example.getman.ui.main.model.HistoryType
import com.example.getman.ui.main.model.RequestModel
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TreeCell

class HistoryRequestTreeCell(
    private val onRequestClick: (RequestModel) -> Unit
) : TreeCell<HistoryType>() {

    override fun updateItem(item: HistoryType?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty) {
            text = null
            graphic = null
        } else {
            item?.let { hisType ->
                val fxmlLoader: FXMLLoader = GetManApplication.instance.loadFxml("request-history.fxml")
                val node = fxmlLoader.load<Node>()
                val requestTypeLabel = node.lookup("#lblRequestType") as Label
                val urlLabel = node.lookup("#lblUrl") as Label
                if (hisType is HistoryType.HistoryDate) {
                    requestTypeLabel.text = hisType.date
                } else if (hisType is HistoryType.HistoryRequest) {
                    requestTypeLabel.text = hisType.request.requestType.value
                    urlLabel.text = hisType.request.url
                    urlLabel.setOnMousePressed {
                        onRequestClick.invoke(hisType.request)
                    }
                }
                graphic = node
            }

        }
    }
}