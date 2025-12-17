module application {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires mysql.connector.j;
    requires com.zaxxer.hikari;
    requires java.sql;


    opens application to javafx.fxml;
    exports application;
    exports application.vue;
    opens application.vue to javafx.fxml;
    exports application.controller;
    opens application.controller to javafx.fxml;
    exports application.DAO;
}