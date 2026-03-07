package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.concurrent.Flow;


public class FileBrowser {

    private static final String DB_URL = "jdbc:sqlite:pete.db"; // same db url throughout

    // ui components
    private FlowPane fileGrid; // flowpanes hold grid of files
    private ComboBox<String> folderComboBox; // combobox holds dropdown to select what folder to view
    private Label statusLabel;

    // data components
    /**
     * figure out how how it loads files from db and then is filtered in memory
     */
    private List<FileRecord> fileRecords;

    /**
     * opens the file browser window
     */
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("P.E.T.E. - Browse Files");

        /**
         * Top dropdown fodler area
         */

        Label folderLabel = new Label("Folder:");

        // dropdown combobox
        folderComboBox = new ComboBox<>();
        folderComboBox.setPrefWidth(250);
        folderComboBox.setPromptText("Select Folder");
        folderComboBox.setOnAction((e) -> filterByFolder); // make a folder filterer

        HBox topHBox = new HBox();
        topHBox.setPadding(new Insets(10));
        topHBox.setAlignment(Pos.CENTER); // may change to right
        topHBox.getChildren().add(folderLabel, folderComboBox);

        /**
         * middle file grid area
         */

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
         * nothing on sides yet?
         */

        /**
         * bottom data / status spot
         */

        statusLabel = new Label("Loading...");
        statusLabel.setStyle("-fx-font-size:11px; fx-text-fill: gray;");
        statusLabel.setPadding(new Insets(10));


        BorderPane root = new BorderPane();
        root.setTop(topHBox);
        root.setCenter(scrollPane);
        root.setBottom(statusLabel);


        /**
         * data?
         */


        // layout & scene -- change from dbviewer.java and change db viewer java, this will be what user sees so
        // make it prettier than the other one

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void filterByFolder() {
        // --
    }
}
