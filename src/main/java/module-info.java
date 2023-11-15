module com.application.labgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.application.labgui to javafx.fxml;
    exports com.application.labgui;

    opens com.application.labgui.Controller to javafx.fxml;
    exports com.application.labgui.Controller;

    opens com.application.labgui.Domain to java.base;
    exports com.application.labgui.Domain;
}
