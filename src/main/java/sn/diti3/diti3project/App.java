package sn.diti3.diti3project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sn.diti3.diti3project.controller.LivreController;

import java.util.Objects;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/livre.fxml"));
        Parent root = loader.load();

        LivreController livreController = loader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("CRUD Livres");
        stage.setScene(scene);
        stage.show();
        // Display the book counts in the console
        livreController.loadTable(); // Load the table data
        livreController.displayBookCounts();
    }
}

