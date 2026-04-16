package org.example;

import javafx.beans.property.*; // Imports multiple classes

/**
 * FileRecord represents a single row from the database.
 * 'Properties' are needed so JavaFX can properly read the info.
 */

public class FileRecord {

    // Private String fileName; would not work, tableview needs its own unique properties
    // Properties are final despite data inside them changing.
    private final IntegerProperty fileId;
    private final StringProperty fileName;
    private final StringProperty fileExt;
    private final StringProperty fileSize;
    private final StringProperty dateModified;
    private final StringProperty folderPath;

    /** Takes DB data and converts it to readable UI format. */
    public FileRecord(int fileId, String fileName, String fileExt,
                      long fileSizeBytes, long dateModifiedTimestamp, String folderPath) {
        this.fileId = new SimpleIntegerProperty(fileId);
        this.fileName = new SimpleStringProperty(fileName);
        this.fileExt = new SimpleStringProperty(fileExt);
        this.fileSize = new SimpleStringProperty(formatFileSize(fileSizeBytes));
        this.dateModified = new SimpleStringProperty(formatDate(dateModifiedTimestamp)); /** Need time filter. */
        this.folderPath = new SimpleStringProperty(folderPath);
    }

    // Property getters for javafx & tableview
    public IntegerProperty fileIdProperty() { return fileId; }
    public StringProperty fileNameProperty() { return fileName; }
    public StringProperty fileExtProperty() { return fileExt; }
    public StringProperty fileSizeProperty() { return fileSize; }
    public StringProperty dateModifiedProperty() { return dateModified; }
    public StringProperty folderPathProperty() { return folderPath; }

    // Normal getters (not for tableview)
    public int getFileId() { return fileId.get(); }
    public String getFileName() { return fileName.get(); }
    public String getFileExt() { return fileExt.get(); }
    public String getFileSize() { return fileSize.get(); }
    public String getDateModified() { return dateModified.get(); }
    public String getFolderPath() { return folderPath.get(); }

    /**  Bytes to normal units conversion. */
    // Should go in helper methods file
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /** Makes timestamps readable. */
    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }
}