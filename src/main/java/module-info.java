module com.just1t.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires fastjson;
    requires jlayer;

    opens com.just1t.ui to javafx.fxml;
    opens com.just1t.dm  to java.base;
    exports com.just1t.ui;
    exports com.just1t.ui.entity;
    opens com.just1t.ui.entity to javafx.fxml;
}