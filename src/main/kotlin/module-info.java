module com.example.getman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires kotlin.stdlib;
    requires java.naming;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires kotlinx.coroutines.core;
    requires okhttp3;
    requires okio;
    requires koin.core;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires net.synedra.validatorfx;
    requires java.desktop;
    requires javafx.swing;
    requires org.jsoup;
    requires com.google.gson;

    opens com.example.getman to javafx.fxml;
    exports com.example.getman;
    opens com.example.getman.ui.login to javafx.fxml;
    opens com.example.getman.ui.register to javafx.fxml;
    opens com.example.getman.ui.main to javafx.fxml;
    opens com.example.getman.ui.main.model to javafx.base;
    opens com.example.getman.data.remote.model to org.hibernate.orm.core;
}