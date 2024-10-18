module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.Frenadol to javafx.fxml;
    exports com.github.Frenadol;
}
