module org.example.sae3_01 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.sae3_01 to javafx.fxml;
    exports org.example.sae3_01;
}