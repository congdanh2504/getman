package com.example.getman.ui.main

import com.example.getman.GetManApplication
import com.example.getman.base.Screen
import com.example.getman.domain.model.CollectionModel
import com.example.getman.extensions.collectIn
import com.example.getman.ui.main.adapter.*
import com.example.getman.ui.main.model.*
import com.example.getman.ui.main.model.RequestModel
import com.example.getman.utils.BodyEnum
import com.example.getman.utils.RequestEnum
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import org.koin.core.inject
import org.kordamp.bootstrapfx.BootstrapFX
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class MainScreen : Screen() {

    lateinit var leftTabPane: TabPane
    lateinit var ivLogout: ImageView
    lateinit var logoutTab: Tab
    lateinit var importJsonBtn: Button
    lateinit var exportJsonBtn: Button
    lateinit var newCollection: Button
    lateinit var btnChooseFile: Button
    lateinit var testGenTable: TableView<Map<String, String>>
    lateinit var ivTestGen: ImageView
    lateinit var testGenTab: Tab
    lateinit var collectionTree: TreeView<CollectionType>
    lateinit var treeView: TreeView<HistoryType>
    lateinit var ivHistory: ImageView
    lateinit var ivCollection: ImageView
    lateinit var historyTab: Tab
    lateinit var collectionTab: Tab
    lateinit var addButton: Tab
    lateinit var tabPane: TabPane
    override val viewModel by inject<MainViewModel>()
    private var tabCount = 0

    override fun onCreate() {
        super.onCreate()
        initViews()
        initListeners()
        bindViewModel()
    }

    private fun initViews() {
        createNewTab()
        val image = Image("ic_collection.png")
        ivCollection.image = image
        val historyImage = Image("ic_history.png")
        ivHistory.image = historyImage
        val testGenImage = Image("ic_analysis.png")
        ivTestGen.image = testGenImage
        val logoutImage = Image("ic_logout.png")
        ivLogout.image = logoutImage
    }

    private fun initListeners() {
        tabPane.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            if (newTab == addButton) {
                createNewTab()
            }
        }
        leftTabPane.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            if (newTab == logoutTab) {
                viewModel.removeUser()
                GetManApplication.instance.navigateToLogin()
            }
        }
        btnChooseFile.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Open Resource File"
            val file = fileChooser.showOpenDialog(GetManApplication.instance.primaryStage)
            if (file != null) {
                try {
                    val testcases = viewModel.generateTestcases(file.absolutePath)
                    testGenTable.items = FXCollections.observableArrayList(testcases)
                    testGenTable.columns.clear()
                    testcases[0].forEach {
                        val column1 = TableColumn<Map<String, String>, String>(it.key)
                        column1.setCellValueFactory { param -> SimpleStringProperty(param.value[it.key]) }
                        testGenTable.columns.add(column1)
                    }
                } catch (e: Exception) {
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "Error"
                        contentText = "Invalid file format"
                    }.show()
                }
            }
        }
        newCollection.setOnAction {
            val tid = TextInputDialog()
            tid.title = "Add a new collection"
            tid.headerText = "Add a new collection"
            tid.contentText = "Enter collection name"
            val result = tid.showAndWait()
            if (result.isPresent) {
                viewModel.addCollection(result.get())
            }
        }
        importJsonBtn.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.json"))

            val file = fileChooser.showOpenDialog(GetManApplication.instance.primaryStage)

            if (file != null) {
                try {
                    val content = String(Files.readAllBytes(Paths.get(file.path)))
                    val gson = GsonBuilder()
                        .registerTypeAdapter(JsonCollection::class.java, CollectionJsonTypeAdapter())
                        .registerTypeAdapter(JsonRequest::class.java, RequestJsonTypeAdapter())
                        .registerTypeAdapter(KeyValueTableModel::class.java, KeyValueTableModelTypeAdapter())
                        .create()

                    val jsonCollection = gson.fromJson(content, JsonCollection::class.java)
                    viewModel.addCollection(jsonCollection)
                } catch (ex: Exception) {
                    Alert(AlertType.ERROR).apply {
                        title = "Error"
                        contentText = "Invalid file format"
                    }.show()
                }
            }
        }
        exportJsonBtn.setOnAction {
            val root: Parent
            try {
                root = GetManApplication.instance.loadFxml("choose-request.fxml").load()
                val stage = Stage()
                stage.title = "Choose item"
                stage.scene = Scene(root, 300.0, 300.0)
                stage.scene.stylesheets.add(BootstrapFX.bootstrapFXStylesheet())
                stage.scene.stylesheets.add(
                    GetManApplication::class.java.getResource("StyleSheet.css")?.toExternalForm()
                )

                val listView = root.lookup("#listView") as ListView<CollectionModel>
                listView.items.addAll(viewModel.collections.value)
                listView.cellFactory = Callback<ListView<CollectionModel>, ListCell<CollectionModel>> {
                    object : ListCell<CollectionModel>() {
                        override fun updateItem(item: CollectionModel?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = item?.name
                        }
                    }
                }

                val okButton: Button = root.lookup("#okBtn") as Button
                val cancelButton: Button = root.lookup("#cancelBtn") as Button

                okButton.setOnAction {
                    val selectedItem: CollectionModel? = listView.selectionModel.selectedItem
                    selectedItem?.let {
                        val gson = GsonBuilder()
                            .registerTypeAdapter(JsonCollection::class.java, CollectionJsonTypeAdapter())
                            .registerTypeAdapter(JsonRequest::class.java, RequestJsonTypeAdapter())
                            .registerTypeAdapter(KeyValueTableModel::class.java, KeyValueTableModelTypeAdapter())
                            .create()
                        val json = gson.toJson(selectedItem.toJsonCollection())
                        val fileChooser = FileChooser()
                        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.json"))

                        val file: File? = fileChooser.showSaveDialog(stage)

                        if (file != null) {
                            try {
                                Files.write(Paths.get(file.path), json.toByteArray())
                            } catch (ex: IOException) {
                                // Handle exception here
                            }
                            stage.close()
                        }
                    }
                }

                cancelButton.setOnAction {
                    stage.close()
                }

                stage.show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun bindViewModel() {
        viewModel.getHistoryRequests()
        viewModel.getCollections()
        viewModel.historyRequest.collectIn(this) {
            val rootItem: TreeItem<HistoryType> = TreeItem()
            var preDate: String? = null
            var subItem: TreeItem<HistoryType>? = null
            it.forEach { historyRequest ->
                val date = Date(historyRequest.time.time)
                val dateString = "${1900 + date.year}-${1 + date.month}-${date.date}"
                if (preDate == null || preDate != dateString) {
                    if (subItem != null) rootItem.children.add(subItem)
                    preDate = dateString
                    subItem = TreeItem(HistoryType.HistoryDate(dateString))
                    subItem?.isExpanded = true
                }
                subItem?.children?.add(
                    TreeItem(
                        HistoryType.HistoryRequest(historyRequest.request)
                    )
                )
            }
            rootItem.children.add(subItem)
            treeView.root = rootItem
            treeView.isShowRoot = false
            treeView.setCellFactory {
                HistoryRequestTreeCell { request ->
                    createNewTab(request)
                }
            }
        }
        viewModel.collections.collectIn(this) {
            val rootCollection: TreeItem<CollectionType> = TreeItem()

            it.forEach { collection ->
                val collectionTitleTree: TreeItem<CollectionType> =
                    TreeItem(CollectionType.CollectionTitle(collection.id!!, collection.name))
                collection.requests.forEach { request ->
                    val subItem: TreeItem<CollectionType> =
                        TreeItem(CollectionType.CollectionRequest(collection.id, request))
                    collectionTitleTree.children.add(subItem)
                }
                collectionTitleTree.isExpanded = true
                rootCollection.children.add(collectionTitleTree)
            }

            collectionTree.root = rootCollection
            collectionTree.isShowRoot = false
            collectionTree.setCellFactory {
                CollectionItemTreeCell({
                    val alert = Alert(AlertType.CONFIRMATION, "Delete selected item?", ButtonType.YES, ButtonType.NO)
                    alert.showAndWait()

                    if (alert.result == ButtonType.YES) {
                        viewModel.deleteCollection(it.id)
                    }
                }, {
                    val alert = Alert(AlertType.CONFIRMATION, "Delete selected item?", ButtonType.YES, ButtonType.NO)
                    alert.showAndWait()

                    if (alert.result == ButtonType.YES) {
                        viewModel.deleteCollectionRequest(it.collectionId, it.request.id!!)
                    }
                }, {
                    createNewTab(it)
                }, {
                    createNewTab()
                    viewModel.isAdding = true
                    viewModel.addingCollectionId = it.id
                })
            }
        }
    }

    private fun createNewTab(requestModel: RequestModel? = null) {
        val fxmlLoader = GetManApplication.instance.loadFxml("new-tab.fxml")
        val newTab = fxmlLoader.load<Tab>()
        tabPane.tabs.add(tabCount++, newTab)
        tabPane.selectionModel.select(newTab)
        newTab.setOnCloseRequest {
            if (tabCount == 1) {
                it.consume()
            } else {
                tabCount--
            }
        }

        requestModel?.let {
            val namespace: ObservableMap<String, Any> = fxmlLoader.namespace
            val tfUrl = namespace["tfUrl"] as TextField
            val cbRequest = namespace["cbRequest"] as ChoiceBox<RequestEnum>
            val paramTable = namespace["paramTable"] as TableView<KeyValueTableModel>
            val requestHeadersTable = namespace["requestHeadersTable"] as TableView<KeyValueTableModel>
            val formDataTable = namespace["formDataTable"] as TableView<KeyValueTableModel>
            val vBoxForm: VBox = namespace["vBoxForm"] as VBox
            val jsonTextArea: TextArea = namespace["jsonTextArea"] as TextArea
            val jsonRadio: RadioButton = namespace["jsonRadio"] as RadioButton
            val formDataRadio: RadioButton = namespace["formDataRadio"] as RadioButton
            val noneRadio: RadioButton = namespace["noneRadio"] as RadioButton

            paramTable.items.addAll(it.params)
            requestHeadersTable.items.addAll(it.headers)
            formDataTable.items.addAll(it.formData)
            cbRequest.value = it.requestType
            tfUrl.text = it.url

            when (it.bodyType) {
                BodyEnum.FORM -> {
                    formDataRadio.isSelected = true
                    vBoxForm.isVisible = true
                    jsonTextArea.isVisible = false
                    jsonTextArea.text = ""
                }
                BodyEnum.JSON -> {
                    jsonRadio.isSelected = true
                    vBoxForm.isVisible = false
                    jsonTextArea.isVisible = true
                }
                BodyEnum.NONE -> {
                    noneRadio.isSelected = true
                    vBoxForm.isVisible = false
                    jsonTextArea.isVisible = false
                    jsonTextArea.text = ""
                }
            }
        }
    }

    companion object {
        const val FXML_VIEW_NAME = "main-page.fxml"
    }
}

typealias Constraint = Pair<Pair<String, String>, Pair<String, List<String>>>