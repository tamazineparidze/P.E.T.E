// 2.21 - touched up the visuals.

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

public class Main extends Application { // inheritance, child(main) extends to parent(application)

    // ui labels for different parameters
    private Label folderLabel;
    private Label resultLabel;
    private Button selectButton;
    private Button scanButton;

    // folder path select
    private String selectedFolderPath = null;

    // indexer instance
    private FileIndexer indexer = new FileIndexer();

    @Override
    public void start(Stage primaryStage) { //javafx startpoint

        // title label
        Label titleLabel = new Label("P.E.T.E. - Personal Electronic Tagging Engine");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // folder select button
        selectButton = new Button("Select Folder");
        selectButton.setPrefWidth(100);
        selectButton.setOnAction(e -> selectFolder(primaryStage));
        // lambda expression, when clicked call selectFolder method with primary stage argument

        // shows selected folderpath
        folderLabel = new Label("No folder selected");
        folderLabel.setStyle("-fx-text-fill: gray;");

        // start scan button
        scanButton = new Button("Start Scan");
        scanButton.setPrefWidth(100);
        scanButton.setDisable(true); // disabled if nothing's selected
        scanButton.setOnAction(e -> startScan());

        /**
         * Decided to keep the view database button instead of replacing it
         */

        // browse files button
        Button browseButton = new Button("Browse Files");
        browseButton.setPrefWidth(100);
        browseButton.setOnAction(e -> openFileBrowser());

        // view database button
        Button viewDatabaseButton = new Button("(Database)");
        viewDatabaseButton.setPrefWidth(73);
        viewDatabaseButton.setOnAction(e -> openDatabaseViewer());

        // result label (shows progress)
        resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 18px;");

        // layout
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER); // centered with 20x30 padding

        root.getChildren().addAll( // add all components to Vbox top to bottom
                titleLabel,
                selectButton,
                folderLabel,
                scanButton,
                browseButton,
                viewDatabaseButton,
                resultLabel
        );

        // scene & stage
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setTitle("P.E.T.E. - MVP2.18");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * opens folder picker
     */
    private void selectFolder(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser(); // javafx's folder picker
        directoryChooser.setTitle("Select Folder to Index");

        // starts folder search at default location
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // home folder (C:/Users/fred)

        // show dialog and wait for me to pick a folder, return null if operation cancelled
        File selectedFolder = directoryChooser.showDialog(stage);
        // when operation is open the code is paused
        if (selectedFolder != null) {
            selectedFolderPath = selectedFolder.getAbsolutePath();  // save full path to instance variable
            folderLabel.setText("Selected: " + selectedFolderPath); // update label to show what's selected
            folderLabel.setStyle("-fx-text-fill: black;");          // was gray, now black
            scanButton.setDisable(false);                     // then enable the scan button to be clicked
            resultLabel.setText(""); // clears previous results
        }
    }

    /**
     * Starts the file scanning process
     */
    private void startScan() {
        // don't scan if no folder's selected
        if (selectedFolderPath == null) {
            resultLabel.setText("Please select a folder first!");
            resultLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        // disable buttons so you cant rescan & get a message instead
        selectButton.setDisable(true);
        scanButton.setDisable(true);
        resultLabel.setText("Scanning folder...");
        resultLabel.setStyle("-fx-text-fill: blue;");

        // run scan in background because if file is large you don't want a frozen window! - make background thread
        new Thread(() -> {
            int filesIndexed = indexer.scanFolder(selectedFolderPath);
            javafx.application.Platform.runLater(() -> {
                resultLabel.setText("Scan complete! Indexed " + filesIndexed + " files.");
                resultLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

                // re-enable the buttons
                selectButton.setDisable(false);
                scanButton.setDisable(false);
            });
        }).start();
    }

    /**
     * opens database viewer window
     */
    private void openDatabaseViewer() {
        DatabaseViewer viewer = new DatabaseViewer();
        viewer.show();
    }

    private void openFileBrowser() {
        FileBrowser fileBrowser = new FileBrowser();
        fileBrowser.show();
    }

    public static void main(String[] args) {
        // check database
        System.out.println("Checking database setup...");
        DatabaseSetup.createTables();
        System.out.println();
        launch(args); // launch favafx
    }
}

/**
 * For me: figure out more in depth on how threading works. still confsued.
 * Labmda expressions will be something I'll need so figure that out too well.
 * Think about css and making it look proper
 */