package com.example.getman.ui.main

import com.example.getman.GetManApplication
import com.example.getman.base.Screen
import com.example.getman.extensions.collectIn
import com.example.getman.ui.main.model.KeyValueTableModel
import com.example.getman.utils.BodyEnum
import com.example.getman.utils.Constants.URL_REGEX_STRING
import com.example.getman.utils.Constants.commonHeaders
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
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import javafx.stage.FileChooser
import kotlinx.coroutines.flow.filterNotNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.koin.core.inject
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InvalidObjectException


class TabScreen : Screen() {
    lateinit var btnChooseFile: Button
    lateinit var btnClearFormData: Button
    lateinit var btnAddFormData: Button
    lateinit var tfFormDataValue: TextField
    lateinit var tfFormDataKey: TextField
    lateinit var formDataValueColumn: TableColumn<KeyValueTableModel, String>
    lateinit var formDataKeyColumn: TableColumn<KeyValueTableModel, String>
    lateinit var formDataTable: TableView<KeyValueTableModel>
    lateinit var jsonTextArea: TextArea
    lateinit var vBoxForm: VBox
    lateinit var jsonRadio: RadioButton
    lateinit var formDataRadio: RadioButton
    lateinit var noneRadio: RadioButton
    lateinit var cBValueHeaders: ComboBox<String>
    lateinit var cBKeyHeaders: ComboBox<String>
    lateinit var btnClearHeaders: Button
    lateinit var btnAddHeaders: Button
    lateinit var requestHeadersValueColumn: TableColumn<KeyValueTableModel, String>
    lateinit var requestHeadersKeyColumn: TableColumn<KeyValueTableModel, String>
    lateinit var requestHeadersTable: TableView<KeyValueTableModel>
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
    private var bodyType = BodyEnum.NONE
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
        noneRadio.isSelected = true
        initTableView()
        initComboBox()
    }

    private fun isValidUrl(url: String): Boolean {
        val urlPattern = URL_REGEX_STRING.toRegex()
        return urlPattern.matches(url)
    }

    private fun initTableView() {
        val keyFactory = PropertyValueFactory<KeyValueTableModel, String>("keyString")
        val valueFactory = PropertyValueFactory<KeyValueTableModel, String>("valueString")
        formDataKeyColumn.cellValueFactory = keyFactory
        formDataValueColumn.cellValueFactory = valueFactory
        requestHeadersKeyColumn.cellValueFactory = keyFactory
        requestHeadersValueColumn.cellValueFactory = valueFactory
        cookieKeyColumn.cellValueFactory = keyFactory
        cookieValueColumn.cellValueFactory = valueFactory
        cookieKeyColumn.cellValueFactory = keyFactory
        cookieValueColumn.cellValueFactory = valueFactory
        paramKeyColumn.cellValueFactory = keyFactory
        paramValueColumn.cellValueFactory = valueFactory
        headerKeyColumn.cellValueFactory = keyFactory
        headerValueColumn.cellValueFactory = valueFactory

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

    private fun initComboBox() {
        cBKeyHeaders.items.addAll(commonHeaders.keys)
        cBValueHeaders.items.addAll(commonHeaders["Accept"]!!.toSet())
        cBKeyHeaders.setOnAction {
            val value = cBKeyHeaders.value.toString()
            cBValueHeaders.items.clear()
            cBValueHeaders.items.addAll(commonHeaders[value]!!.toSet())
        }
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
        btnAddHeaders.setOnAction {
            val key = cBKeyHeaders.value.toString()
            val value = cBValueHeaders.value.toString()
            if (key.isNotBlank() && value.isNotBlank()) {
                requestHeadersTable.items.add(KeyValueTableModel(key, value))
            }
        }
        btnAddFormData.setOnAction {
            val key = tfFormDataKey.text
            val value = tfFormDataValue.text
            if (key.isNotBlank() && value.isNotBlank()) {
                formDataTable.items.add(KeyValueTableModel(key, value))
                tfFormDataKey.text = ""
                tfFormDataValue.text = ""
            }
        }
        btnClearFormData.setOnAction {
            formDataTable.items.clear()
        }
        btnSend.setOnAction {
            if (isValidUrl(tfUrl.text)) {
                val url = tfUrl.text
                val headers = mutableMapOf<String, String>()
                requestHeadersTable.items.forEach {
                    headers[it.getKeyString()] = it.getValueString()
                }
                val jsonText = jsonTextArea.text

                val requestBody: RequestBody = when (bodyType) {
                    BodyEnum.JSON, BodyEnum.NONE -> {
                        val jsonMediaType: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
                        jsonText.toRequestBody(jsonMediaType)
                    }
                    BodyEnum.FORM_DATA -> {
                        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                        formDataTable.items.forEach {
                            if (it.getValueString().startsWith("File: ")) {
                                val file = File(it.getValueString().replaceFirst("File: ", ""))
                                val fileBody: RequestBody =
                                    file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                                builder.addFormDataPart(it.getKeyString(), file.name, fileBody)
                            } else {
                                builder.addFormDataPart(it.getKeyString(), it.getValueString())
                            }

                        }
                        builder.build()
                    }
                }
                viewModel.request(
                    RequestModel(
                        cbRequest.value,
                        url,
                        headers,
                        requestBody
                    )
                )
            } else {
                println("Invalid URL format")
            }
        }
        btnClearHeaders.setOnAction {
            requestHeadersTable.items.clear()
        }
        btnClearQuery.setOnAction {
            paramTable.items.clear()
        }
        noneRadio.setOnAction {
            vBoxForm.isVisible = false
            jsonTextArea.isVisible = false
            bodyType = BodyEnum.NONE
            jsonTextArea.text = ""
        }
        jsonRadio.setOnAction {
            vBoxForm.isVisible = false
            jsonTextArea.isVisible = true
            bodyType = BodyEnum.JSON
        }
        formDataRadio.setOnAction {
            vBoxForm.isVisible = true
            jsonTextArea.isVisible = false
            bodyType = BodyEnum.FORM_DATA
            jsonTextArea.text = ""
        }
        btnChooseFile.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Resource File"
            val file = fileChooser.showOpenDialog(GetManApplication.instance.primaryStage)
            if (file != null) {
                tfFormDataValue.text = "File: ${file.absolutePath}"
            }
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

    private fun detectAndFormat(text: String): String {
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