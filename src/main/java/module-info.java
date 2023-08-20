module sn.diti3.diti3project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires TrayNotification;


    opens sn.diti3.diti3project.DB to javafx.fxml;
    exports sn.diti3.diti3project.DB;
    opens sn.diti3.diti3project.controller to javafx.fxml;
    exports sn.diti3.diti3project.controller;
    opens sn.diti3.diti3project.entity to javafx.fxml;
    exports sn.diti3.diti3project.entity;
    opens sn.diti3.diti3project to javafx.fxml;
    exports sn.diti3.diti3project;
}