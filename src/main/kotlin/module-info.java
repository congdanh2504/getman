module com.example.getman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires kotlinx.coroutines.core;
    requires okhttp3;
    requires okio;
    requires koin.core;

    opens com.example.getman to javafx.fxml;
    exports com.example.getman;
    exports com.example.getman.controllers;
}