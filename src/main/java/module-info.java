module edu.upvictoria.javasqlide {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires ExceptionFramework;
    requires java.sql;
    requires org.fxmisc.richtext;
    requires reactfx;

    opens edu.upvictoria.javasqlide.controllers to javafx.fxml;
    exports edu.upvictoria.javasqlide;
}