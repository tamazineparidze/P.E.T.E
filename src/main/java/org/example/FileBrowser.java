package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import java.util.List;

/**
 * grid view for browsing the files that were indexed filtered by folder
 */
public class FileBrowser {

    private static final String DB_URL = "jdbc:sqlite:pete.db"; // same db url throughout
    // ui components
    private FlowPane fileGrid; // flowpanes hold grid of files
    private ComboBox<String> folderComboBox; // combobox holds dropdown to select what folder to view
    private Label statusLabel;
    // data components
    private Map<String, Integer> folderMap; //maps folder path to file id
    private List<FileRecord> allFiles; //list of every file from database (loaded once)
    private Map<String, Image> thumbnailCache; // one time delay of opening images then its in memory

    private int missingFileCount = 0; //track missing files so it doesnt take a year to display messages in terminal
    /**
     * opens the file browser window
     */
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("P.E.T.E. - Browse Files");
        Label folderLabel = new Label("Folder: "); //label next to dropdown

        thumbnailCache = new HashMap<>(); //initialize thumbnailscache

        // dropdown combobox (dropdown menu itself)
        folderComboBox = new ComboBox<>();
        folderComboBox.setPrefWidth(350);
        folderComboBox.setOnAction((e) -> filterByFolder());

        HBox topHBox = new HBox();
        topHBox.setPadding(new Insets(12));
        topHBox.setAlignment(Pos.CENTER_LEFT);
        topHBox.getChildren().addAll(folderLabel, folderComboBox);

        // grid representation of files with flowpane
        fileGrid = new FlowPane();

        fileGrid.setHgap(8);
        fileGrid.setVgap(8);
        fileGrid.setPadding(new Insets(8));
        fileGrid.setAlignment(Pos.TOP_LEFT);

        // a scroller to scroll through many files with a white background
        ScrollPane scrollPane = new ScrollPane(fileGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");

        // bottom - status pane
        statusLabel = new Label("Loading...");
        statusLabel.setStyle("-fx-font-size:11px; fx-text-fill: gray;");
        statusLabel.setPadding(new Insets(12));

        //layout for borderpane and how the filebrowser will be divided
        BorderPane root = new BorderPane();
        root.setTop(topHBox);
        root.setCenter(scrollPane);
        root.setBottom(statusLabel);

        // load folders list for the combobox dropdown, load files from database, and display files in the grid
        loadFolders();
        loadAllFiles();
        displayFiles(allFiles);

        Scene scene = new Scene(root, 1150, 800);
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
                    file.file_ext,
                    file.file_size,
                    file.date_modified,
                    folder.path_name,
                    folder.folder_id
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
        missingFileCount = 0;

        //create card for each file to add to grid
        for (FileRecord file : files) {
            //return a vbox containing file parameters and add to flowpane
            VBox card = createFileCard(file);
            fileGrid.getChildren().add(card);
        }

        //status update + count
        String folderText = folderComboBox.getValue();
        String statusText = ("Showing " + files.size() + " files from: " + folderText);

        if (missingFileCount > 0) {
            statusText += missingFileCount + " files missing";
        }
        statusLabel.setText(statusText);

        if (missingFileCount > 0 ) { // print to console
            System.out.println(missingFileCount + " files missing");
        }
    }

    /**
     * Create a visual card for singular file returns vbox w paramteres
     */
    private VBox createFileCard(FileRecord file) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(86,115);
        card.setMaxSize(86,115 );
        card.setStyle(
                "-fx-border-color: #CCCCCC; "
                + "-fx-border-width: 1px; "
                + "-fx-background-color: #FFFFFF; "
                + "-fx-padding: 8"
        );

        ImageView thumbnail = getThumbnail(file);

        //filename truncated if too long
        String displayName = file.getFileName();
        if (displayName.length() > 10) {
            displayName = displayName.substring(0, 8) + "...";
        }
        Label nameLabel = new Label(displayName);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);

        //filesize
        Label sizeLabel = new Label(file.getFileSize());
        sizeLabel.setStyle("-fx-font-size: 11px;  -fx-text-fill: gray;");

        //extensions
        Label extLabel = new Label(file.getFileExt());
        extLabel.setStyle("-fx-font-size: 10px;");

        card.getChildren().addAll(thumbnail, nameLabel, sizeLabel, extLabel);

        //when mouse enters card area
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-border-color: #329AFB; " +
                            "-fx-border-width: 2px; " +
                            "-fx-background-color: #F5FDFF; " +
                            "-fx-padding: 8;"
            );
            card.setCursor(javafx.scene.Cursor.HAND);
        });

        //when mouse leaves card area
        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-border-color: #CCCCCC; " +
                            "-fx-border-width: 1px; " +
                            "-fx-background-color: #FFFFFF; " +
                            "-fx-padding: 8;"
            );
            card.setCursor(javafx.scene.Cursor.DEFAULT);
        });

        //double click to open
        card.setOnMouseClicked(e -> {
            //get double click info
            if (e.getClickCount() == 2) {
                openFile(file); // method below
            }
        });

        return card;
    }

    /**
     * Opens file using default application
     */
    private void openFile(FileRecord file) {
        try {
            // combine filepath and filename and create file object
            String fullPath = file.getFolderPath() + File.separator + file.getFileName();
            File fileToOpen = new File(fullPath);

            // checks if file exists
            if (!fileToOpen.exists()) {
                System.out.println("File " + fullPath + " does not exist");
                statusLabel.setText("File " + fullPath + " not found");
                return;
            }

            // checks if desktop default app is supported and opens with default app
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fileToOpen);

                System.out.println("Opened " + fullPath + " successfully");
            } else  {
                System.out.println("Opening " + fullPath + " not supported");
            }

        } catch (Exception e) { // cant open / error
            System.out.println("Error opening: " + file.getFileName());
            e.printStackTrace();
            statusLabel.setText("Error opening: " + file.getFileName());
        }
    }
    /**
     * add thumbnail or some kind of symbol to finish it off.
     */
    private ImageView getThumbnail(FileRecord file) {
        // check if it's an image
        String ext = file.getFileExt();
        if (ext == null || ext.isEmpty()) {
            return createPlaceholderIcon();
        }

        ext = ext.toLowerCase();

        boolean isImage = ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".gif") || ext.equals(".raw") || ext.equals(".nef") || ext.equals(".raf");

        if (!isImage) {
            return createPlaceholderIcon();
        }
        // if image build path and check cache
        String fullPath = file.getFolderPath() + File.separator + file.getFileName();

        Image cachedImage = thumbnailCache.get(fullPath);
        if (cachedImage != null) { // if in cache return
            ImageView imageView = new ImageView(cachedImage);
            imageView.setFitHeight(75);
            imageView.setFitWidth(75);
            imageView.setPreserveRatio(true);
            return imageView;
        }
        // if not in cache load it
        // figure out how to load it
        try {
            File imageFile = new File(fullPath);
            // does it not exist?
            if (!imageFile.exists()) {
                missingFileCount++;
                return createPlaceholderIcon();
            }
            // checks if file size is more than 50mb (may change depending on how slow/fast it is and ram usage)
            long fileSizeBytes = imageFile.length();
            long maxSize = 50 * 1024 * 1024;
            if (fileSizeBytes > maxSize) {
                System.out.println("File " + fullPath + " is too large");
                return createPlaceholderIcon();
            }
            //load the image
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            // create an iamge with auto resize
            Image thumbnail = new Image(fileInputStream, 75, 75, true, true);
            fileInputStream.close();
            //cache the thumbnail
            thumbnailCache.put(fullPath, thumbnail);
            // create and return imageview
            ImageView imageView = new ImageView(thumbnail);
            imageView.setFitHeight(75);
            imageView.setFitWidth(75);
            imageView.setPreserveRatio(true);

            // add more css??

            return imageView;

        } catch (Exception e) {
            System.out.println("Error loading thumbnail for: " + fullPath);
            e.printStackTrace();
            return createPlaceholderIcon();
        }

    }
    // display placehorder with colored rectangle as background
    private ImageView createPlaceholderIcon() {
        ImageView placeholder = new ImageView();
        placeholder.setFitHeight(86);
        placeholder.setFitWidth(86);
        placeholder.setStyle("-fx-border-color: #DDDDDD; -fx-border-width: 1px;");
        return placeholder;

    }

}

