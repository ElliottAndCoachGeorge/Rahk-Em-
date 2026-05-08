module com.elliottandcoachgeorge.javafxtest {

    requires javafx.controls;
    requires javafx.fxml;

    opens com.elliottandcoachgeorge.javafxtest to javafx.fxml;

    exports com.elliottandcoachgeorge.javafxtest;
}