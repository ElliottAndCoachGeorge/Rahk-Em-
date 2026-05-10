module com.elliottandcoachgeorge.javafxtest {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.elliottandcoachgeorge.javafxtest to javafx.fxml;

    exports com.elliottandcoachgeorge.javafxtest;
    exports com.elliottandcoachgeorge.javafxtest.Controllers;
    opens com.elliottandcoachgeorge.javafxtest.Controllers to javafx.fxml;
}