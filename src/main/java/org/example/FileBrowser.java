package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.concurrent.Flow;


public class FileBrowser {

    private static final String DB_URL = "jdbc:sqlite:pete.db"; // same db url throughout

    // ui components
    private FlowPane fileGrid; // flowpanes hold grid of files
    private ComboBox<String> folderComboBox; // combobox holds dropdown to select what folder to view

    // data components

    /**
     * opens the file browser window
     */
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("P.E.T.E. - Browse Files");


        // dropdown combobox
        folderComboBox = new ComboBox<>();
        folderComboBox.setPrefWidth(250);
        folderComboBox.setPromptText("Select Folder");
        folderComboBox.setOnAction((e) -> filter_something_someting);


        // grid representation of files with flowpane
        fileGrid = new FlowPane();
        fileGrid.setHgap(10);
        fileGrid.setVgap(10);
        fileGrid.setPadding(new Insets(10));
        fileGrid.setAlignment(Pos.TOP_LEFT);

        // a scroller to scroll through many files with a white background
        ScrollPane scrollPane = new ScrollPane(fileGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        /**
         * have a top hbox with folder dropdown
         * center flowpane that is scrollable
         * and a label/data section on bottom
         * nothing on sides yet?
         */


        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);


        // layout & scene -- change from dbviewer.java and change db viewer java, this will be what user sees so
        // make it prettier than the other one
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(tableView, statusLabel);


        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }
}
