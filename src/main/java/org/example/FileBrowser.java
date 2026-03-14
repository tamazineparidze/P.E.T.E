// 2.17 - found & fixed bugs!
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

import java.sql.*;
import java.util.*;


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
     * figure out how it loads files from db and then is filtered in memory
     */
    private Map<String, Integer> folderMap; //maps folder path to file id
    private List<FileRecord> allFiles; //list of every file from database (loaded once)

    /**
     * opens the file browser window
     */
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("P.E.T.E. - Browse Files");

        Label folderLabel = new Label("Folder: "); //label next to dropdown

        // dropdown combobox (dropdown menu itself)
        folderComboBox = new ComboBox<>();
        folderComboBox.setPrefWidth(350);
        folderComboBox.setPromptText("Select Folder");
        folderComboBox.setOnAction((e) -> filterByFolder()); // make a folder filterer

        HBox topHBox = new HBox();
        topHBox.setPadding(new Insets(10));
        topHBox.setAlignment(Pos.CENTER);
        topHBox.getChildren().addAll(folderLabel, folderComboBox);

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

        // bottom - status pane
        statusLabel = new Label("Loading...");
        statusLabel.setStyle("-fx-font-size:11px; fx-text-fill: gray;");
        statusLabel.setPadding(new Insets(10));

        //layout for borderpane and how the filebrowser will be divided
        BorderPane root = new BorderPane();
        root.setTop(topHBox);
        root.setCenter(scrollPane);
        root.setBottom(statusLabel);

        // load folders list for the combobox dropdown
        loadFolders();

        // load files for from database
        loadAllFiles();

        // display the files in the grid i have planned
        displayFiles(allFiles);

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

        // try database connection, sql executor, and query results - pasted from dbviewer.java
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // add all files option
            folderComboBox.getItems().add("All Files");

            //map all files to
            folderMap.put("All Files", -1); //-1 means show all

            //loop through the database
            while (rs.next()) {
                int folderId = rs.getInt("folder_id");
                String folderPath = rs.getString("path_name");
                folderComboBox.getItems().add(folderPath);
                folderMap.put(folderPath, folderId);
            }

            folderComboBox.getSelectionModel().selectFirst();

        } catch (Exception e) {
            System.out.println("Error loading file browser");
            e.printStackTrace();
        }
    }

    /**
     * load files from database & join file and folder tables
     */

    private void loadAllFiles() {
        allFiles = new ArrayList<>();

        // sql join to combine file and folder info into one query
        String sql = """
                SELECT
                    file.file_id,
                    file.file_name,
                    file.file_extension,
                    file.file_size,
                    file.date_modified,
                    folder.path_name,
                    folder.folder_id,
                FROM file
                JOIN folder ON file.folder_id = folder.folder_id
                ORDER BY file.file_name
                """;
        // same as before
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
                 while (rs.next()) {
                     // create filerecord from database
                     FileRecord record = new FileRecord(
                             rs.getInt("file_id"),
                             rs.getString("file_name"),
                             rs.getString("file_ext"),
                             rs.getLong("file_size"),
                             rs.getLong("date_modified"),
                             rs.getString("path_name")
                             );

                     allFiles.add(record);
                 }

                 System.out.println(" Loaded " + allFiles.size() + " files");
        } catch (Exception e) {
                 System.out.println("Error loading file browser");
                 e.printStackTrace();
        }
    }

    /**
     * filters files based on the selected dropdown folder
     */

    private void filterByFolder() {
        // get currently selected folder from dropdown that returns whatever user selects
        String selectedFolder = folderComboBox.getValue();

        // check if all files is selected and show everything if so, else filter specifically
        if (selectedFolder.equals("All Files")) {
            displayFiles(allFiles);
        } else {
            // make empty list for filtered files
            List<FileRecord> filteredFiles = new ArrayList<>();

            //loop through all files and check if file folder matches select folder
            for (FileRecord file : allFiles) {
                if (file.getFolderPath().equals(selectedFolder)) {
                    filteredFiles.add(file);
                }
            }
            //display only the filtered ones
            displayFiles(filteredFiles);
        }
    }

    /**
     * filters files in grid
     */

    private void displayFiles(List<FileRecord> files) {
        fileGrid.getChildren().clear(); //clear previous cards
        //create card for each file to add to grid
        for (FileRecord file : files) {

            VBox card = createFileCard(file);
        }

    }

    /**
     * Create a visul card for singular file
     */

    private VBox createFileCard(FileRecord file) {
        VBox card = new VBox(8);

        return card;
    }
}

