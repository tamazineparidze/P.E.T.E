// creates a window that displas indexed files in a table, runs when "view database" is clicked

package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseViewer {

    private static final String DB_URL = "jdbc:sqlite:pete.db"; // samedb url throughout

    /**
     * new window to show indexed files
     */
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("P.E.T.E. - Database Viewer");

        // display a filerecord object per row
        TableView<FileRecord> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // column shows string from filerecord. show header text. get filerecord object and filename property of object
        TableColumn<FileRecord, String> fileNameCol = new TableColumn<>("File Name");
        fileNameCol.setCellValueFactory(data -> data.getValue().fileNameProperty());
        fileNameCol.setPrefWidth(200);

        TableColumn<FileRecord, String> fileExtCol = new TableColumn<>("Extension");
        fileExtCol.setCellValueFactory(data -> data.getValue().fileExtProperty());
        fileExtCol.setPrefWidth(80);

        TableColumn<FileRecord, String> fileSizeCol = new TableColumn<>("Size");
        fileSizeCol.setCellValueFactory(data -> data.getValue().fileSizeProperty());
        fileSizeCol.setPrefWidth(100);

        TableColumn<FileRecord, String> dateModifiedCol = new TableColumn<>("Date Modified");
        dateModifiedCol.setCellValueFactory(data -> data.getValue().dateModifiedProperty());
        dateModifiedCol.setPrefWidth(150);

        TableColumn<FileRecord, String> folderPathCol = new TableColumn<>("Folder");
        folderPathCol.setCellValueFactory(data -> data.getValue().folderPathProperty());
        folderPathCol.setPrefWidth(300);

        // add columns to table
        tableView.getColumns().addAll(fileNameCol, fileExtCol, fileSizeCol, dateModifiedCol, folderPathCol);

        // like arraylist (I dislike comp228) but javafx watches it for changes and updates automatically
        ObservableList<FileRecord> fileRecords = loadFilesFromDatabase();
        tableView.setItems(fileRecords);

        // status
        Label statusLabel = new Label("Showing " + fileRecords.size() + " files");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // layout & scene
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(tableView, statusLabel);

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads all files from the database
     */
    private ObservableList<FileRecord> loadFilesFromDatabase() {
        ObservableList<FileRecord> records = FXCollections.observableArrayList(); // empty list
        //sql join query
        String sql = """
            SELECT 
                file.file_id,
                file.file_name,
                file.file_ext,
                file.file_size,
                file.date_modified,
                folder.path_name
            FROM file
            JOIN folder ON file.folder_id = folder.folder_id
            ORDER BY file.file_name
            """;
        // try database connection, sql executor, and query results
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // loops through rows
            while (rs.next()) { // create file record take raw db values
                FileRecord record = new FileRecord(
                        rs.getInt("file_id"),       //1
                        rs.getString("file_name"),  // fred.jpg
                        rs.getString("file_ext"),   // .jpg
                        rs.getLong("file_size"),    // 1232349820
                        rs.getLong("date_modified"),// 179203854422300
                        rs.getString("path_name")   // C:/Photos
                );
                records.add(record);
            }
            System.out.println("Loaded " + records.size() + " files from database");

        } catch (Exception e) { // if error print details, return whatever was loaded
            System.out.println("Error loading files:");
            e.printStackTrace();
        }
        return records;
    }
}

/**
 * For me: Display of javafx visuals makes sense, lacking depth on sql join and interactions with resultset
 * get a better understanding of column extraction and how data binds to UI
 */