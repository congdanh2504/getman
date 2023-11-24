package com.example.getman.ui.main

import com.example.getman.base.Screen
import com.example.getman.extensions.collectIn
import com.example.getman.ui.main.model.KeyValueTableModel
import com.example.getman.utils.Constants.URL_REGEX_STRING
import com.example.getman.utils.FormatUtils.formatHtml
import com.example.getman.utils.FormatUtils.formatJson
import com.example.getman.utils.RequestEnum
import com.example.getman.utils.StringUtils
import com.google.gson.JsonSyntaxException
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.web.WebView
import kotlinx.coroutines.flow.filterNotNull
import okhttp3.Response
import org.koin.core.inject
import java.io.ByteArrayInputStream
import java.io.InvalidObjectException

class TabScreen : Screen() {
    lateinit var prettyTextArea: TextArea
    lateinit var cookieValueColumn: TableColumn<KeyValueTableModel, String>
    lateinit var cookieKeyColumn: TableColumn<KeyValueTableModel, String>
    lateinit var cookieTable: TableView<KeyValueTableModel>
    lateinit var headerValueColumn: TableColumn<KeyValueTableModel, String>
    lateinit var headerKeyColumn: TableColumn<KeyValueTableModel, String>
    lateinit var headersTable: TableView<KeyValueTableModel>
    lateinit var btnClearQuery: Button
    lateinit var scrollImagePreview: ScrollPane
    lateinit var contentPane: TabPane
    lateinit var previewImage: ImageView
    lateinit var webview: WebView
    lateinit var btnAddQuery: Button
    lateinit var tfValue: TextField
    lateinit var tfKey: TextField
    lateinit var paramValueColumn: TableColumn<KeyValueTableModel, String>
    lateinit var paramKeyColumn: TableColumn<KeyValueTableModel, String>
    lateinit var paramTable: TableView<KeyValueTableModel>
    lateinit var textArea: TextArea
    lateinit var bodyTab: Tab
    lateinit var tfUrl: TextField
    lateinit var btnSend: Button
    lateinit var cbRequest: ChoiceBox<RequestEnum>
    lateinit var closeLabel: Label
    override val viewModel by inject<MainViewModel>()

    override fun onCreate() {
        super.onCreate()
        initViews()
        initListeners()
        bindViewModel()
    }

    private fun initViews() {
        val requestNames
                : ObservableList<RequestEnum> = FXCollections.observableArrayList(*RequestEnum.values())
        cbRequest.items = requestNames
        cbRequest.value = RequestEnum.GET
        initTableView()
    }

    private fun isValidUrl(url: String): Boolean {
        val urlPattern = URL_REGEX_STRING.toRegex()
        return urlPattern.matches(url)
    }

    private fun initTableView() {
        cookieKeyColumn.cellValueFactory = PropertyValueFactory("keyString")
        cookieValueColumn.cellValueFactory = PropertyValueFactory("valueString")
        paramKeyColumn.cellValueFactory = PropertyValueFactory("keyString")
        paramValueColumn.cellValueFactory = PropertyValueFactory("valueString")
        headerKeyColumn.cellValueFactory = PropertyValueFactory("keyString")
        headerValueColumn.cellValueFactory = PropertyValueFactory("valueString")
        paramTable.items.addListener(ListChangeListener {
            val newURL = StringBuilder(tfUrl.text.substringBefore('?'))
            it.list.forEachIndexed { index, queryParam ->
                if (index == 0) {
                    newURL.append("?")
                } else {
                    newURL.append("&")
                }
                newURL.append("${queryParam.getKeyString()}=${queryParam.getValueString()}")
            }
            tfUrl.text = newURL.toString()
        })
    }

    private fun initListeners() {
        btnAddQuery.setOnAction {
            val key = tfKey.text
            val value = tfValue.text
            if (key.isNotBlank() && value.isNotBlank()) {
                paramTable.items.add(KeyValueTableModel(key, value))
                tfKey.text = ""
                tfValue.text = ""
            }
        }
        btnSend.setOnAction {
            if (isValidUrl(tfUrl.text)) {
                viewModel.request(tfUrl.text)
            } else {
                println("Invalid URL format")
            }
        }
        btnClearQuery.setOnAction {
            paramTable.items.clear()
        }
    }

    private fun bindViewModel() {
        viewModel.response.filterNotNull().collectIn(this) {
            handleBody(it)
            handleCookies(it)
            handleHeaders(it)
        }
    }

    private fun handleBody(response: Response) {
        val contentType: String? = response.header("Content-Type")
        var isImage = false
        if (contentType != null) {
            isImage = contentType.startsWith("image/")
        }
        contentPane.isVisible = !isImage
        scrollImagePreview.isVisible = isImage
        if (isImage) {
            previewImage.image = Image(ByteArrayInputStream(response.body?.bytes()))
        } else {
            response.body?.string()?.let { content ->
                textArea.text = content
                prettyTextArea.text = detectAndFormat(content)
                webview.engine.loadContent(content)
            }
        }
    }

    private fun handleCookies(response: Response) {
        cookieTable.items.clear()
        cookieTable.items.addAll(response.headers.values("Set-Cookie").map {
            val parts = it.split("=", limit = 2)
            KeyValueTableModel(parts[0], StringUtils.insertNextLine(parts[1]))
        })
    }

    private fun handleHeaders(response: Response) {
        headersTable.items.clear()
        headersTable.items.addAll(response.headers.map {
            KeyValueTableModel(it.first, StringUtils.insertNextLine(it.second))
        })
    }

    fun detectAndFormat(text: String): String {
        return try {
            // Try to parse and format as JSON
            formatJson(text)
        } catch (e: JsonSyntaxException) {
            try {
                // If it's not JSON, try to parse and format as HTML
                formatHtml(text)
            } catch (e: Exception) {
                throw InvalidObjectException(e.message)
            }
        }
    }
}