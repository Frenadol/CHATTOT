module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    exports com.github.Frenadol.view;
    opens com.github.Frenadol to javafx.fxml;
    exports com.github.Frenadol;
    opens com.github.Frenadol.view to javafx.fxml;
}
