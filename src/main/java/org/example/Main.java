// 5.2 - Date range works - have future ideas of adding quick filters like yesterday / last week filters

package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * Entrypoint for P.E.T.E. - indexing and folder selection.
 */

public class Main extends Application {

    // ----- UI labels ----- //
    private Label folderLabel;
    private Label resultLabel;
    private Button selectButton;
    private Button scanButton;

    // ----- Stores absolute path of directory ----- //
    private String selectedFolderPath = null;

    // ----- Logic for scanning and saving files to Pete.DB ----- //
    private FileIndexer indexer = new FileIndexer();

    /** Primary window for P.E.T.E. */
    @Override
    public void start(Stage primaryStage) {

        // UI header
        Label titleLabel = new Label("P.E.T.E. - Personal Electronic Tagging Engine");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Folder select button
        selectButton = new Button("Select Folder");
        selectButton.setPrefWidth(100);
        // Lambda -> when clicked open OS folder select screen
        selectButton.setOnAction(e -> selectFolder(primaryStage));

        // Folder selection label
        folderLabel = new Label("No folder selected");
        folderLabel.setStyle("-fx-text-fill: gray;");

        // Start scan button
        scanButton = new Button("Start Scan");
        scanButton.setPrefWidth(100);
        scanButton.setDisable(true); // Disabled if nothing's selected
        scanButton.setOnAction(e -> startScan());

        // Browse files button
        Button browseButton = new Button("Browse Files");
        browseButton.setPrefWidth(100);
        browseButton.setOnAction(e -> openFileBrowser());

        // View database button
        Button viewDatabaseButton = new Button("(Database)");
        viewDatabaseButton.setPrefWidth(73);
        viewDatabaseButton.setOnAction(e -> openDatabaseViewer());

        // Status area
        resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 18px;");

        // Layout assembly
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        // Components in order from top to bottom
        root.getChildren().addAll(
                titleLabel,
                selectButton,
                folderLabel,
                scanButton,
                browseButton,
                viewDatabaseButton,
                resultLabel
        );

        // Final scene display
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setTitle("P.E.T.E. - MVP4.11");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** Opens a native folder picker. */
    private void selectFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser(); // javafx's folder picker
        directoryChooser.setTitle("Select Folder to Index");

        // Starts folder search at default 'home' location
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFolder = directoryChooser.showDialog(stage);

        // Store path and update the button to reflect current state
        if (selectedFolder != null) {
            selectedFolderPath = selectedFolder.getAbsolutePath();
            folderLabel.setText("Selected: " + selectedFolderPath);
            folderLabel.setStyle("-fx-text-fill: black;");
            scanButton.setDisable(false);
            resultLabel.setText("");
        }
    }

    /** Starts the indexing process. */
    private void startScan() {
        // Can't scan if no folder is selected
        if (selectedFolderPath == null) {
            resultLabel.setText("Please select a folder first!");
            resultLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        // Prevent multiple scans at once
        selectButton.setDisable(true);
        scanButton.setDisable(true);
        resultLabel.setText("Scanning folder...");
        resultLabel.setStyle("-fx-text-fill: blue;");

        // Run scan in background, else if file is large, window will be frozen! - Make background thread.
        new Thread(() -> {
            int filesIndexed = indexer.scanFolder(selectedFolderPath);
            javafx.application.Platform.runLater(() -> {
                resultLabel.setText("Scan complete! Indexed " + filesIndexed + " files.");
                resultLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                selectButton.setDisable(false);
                scanButton.setDisable(false);
            });
        }).start();
    }

    /** Opens database window. */
    private void openDatabaseViewer() {
        DatabaseViewer viewer = new DatabaseViewer();
        viewer.show();
    }

    /** Opens file browser window. */
    private void openFileBrowser() {
        FileBrowser fileBrowser = new FileBrowser();
        fileBrowser.show();
    }

    /** Amen */
    public static void main(String[] args) {
        System.out.println("Checking database setup...");
        DatabaseSetup.createTables();
        System.out.println();
        launch(args);
    }
}