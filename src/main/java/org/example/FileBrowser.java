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

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Flow;


/**
 * Planned the look of the filebrowser. Also have an image of it in my mind that resembles the deliverable.
 * research how to make methods work on top of the grid display being set up properly
 */

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
    private Map<String, Integer> folderMap; //maps folder path to file id
    private List<FileRecord> fileRecords; //list of every file from database (loaded once)

    /**
     * opens the file browser window
     */
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("P.E.T.E. - Browse Files");

        /** top bar - folder dropdown */

        Label folderLabel = new Label("Folder:"); //label next to dropdown

        // dropdown combobox (dropdown menu itself)
        folderComboBox = new ComboBox<>();
        folderComboBox.setPrefWidth(250);
        folderComboBox.setPromptText("Select Folder");
        //when user selects different folder, calls filterByFolder
        folderComboBox.setOnAction((e) -> filterByFolder); // make a folder filterer

        HBox topHBox = new HBox();
        topHBox.setPadding(new Insets(10));
        topHBox.setAlignment(Pos.CENTER); // may change to right
        topHBox.getChildren().add(folderLabel, folderComboBox);

        /** middle - filegrid main content area */

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

        /** bottom - status / info */


        statusLabel = new Label("Loading...");
        statusLabel.setStyle("-fx-font-size:11px; fx-text-fill: gray;");
        statusLabel.setPadding(new Insets(10));

        //layout for borderpane and how the filebrowser will be divided
        BorderPane root = new BorderPane();
        root.setTop(topHBox);
        root.setCenter(scrollPane);
        root.setBottom(statusLabel);

        /** loading data from database */

        // load folders list for the combobox dropdown

        // load files for from database

        // display the files in the grid i have planned

        // layout & scene -- change from dbviewer.java and change db viewer java
        // this will be what user sees, so make it prettier than the other one

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * loads folders from database and fills the dropdown selection
     */
    private void loadFolders() {

        folderMap = new HashMap<>(); // stores folderpath as string and folderid as integer

        //sql to get all folders alphabetically
        String sql = "SELECT folder_id, path_name FROM folder ORDER BY path_name";

        try (Connection conn = DriverManager.getConnection(DB_URL);)


    } catch



    }

    /**
     * filters files based on the selected dropdown folder
     */

    private void filterByFolder() {
        // --
    }

}
