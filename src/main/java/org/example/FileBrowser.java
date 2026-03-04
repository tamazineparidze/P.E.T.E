package org.example;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.util.List;
import java.util.concurrent.Flow;


public class FileBrowser {

    private static final String DB_URL = "jdbc:sqlite:pete.db"; // same db url throughout

    // ui components
    private FlowPane fileGrid;

    // data components

    /**
     * opens the file browser window
     */
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("P.E.T.E. - Browse Files");


        // grid representation of files
        fileGrid = new FlowPane();
        fileGrid.setHgap(10);
        fileGrid.setVgap(10);
        fileGrid.setPadding(-----);
        fileGrid.setAlignment(----);

        // a scroller to scroll through many files


        BorderPane root = new BorderPane();


        Scene scene = new Scene(----, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
