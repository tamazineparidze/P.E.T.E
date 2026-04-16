// scans folders, extracts metadata, stores in pete.db
// runs when I click start scan

package org.example;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans a folder and indexes all files to pete.db.
 */

public class FileIndexer {

    private static final String DB_URL = "jdbc:sqlite:pete.db"; // Pete will repeat the same database

    /** Open database connection and recursive search */
    public int scanFolder(String folderPath) {
        System.out.println("Starting scan of: " + folderPath);
        // Opens connection to database & closes when done
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // 1. Add folder into the folder table
            int folderId = insertFolder(conn, folderPath);
            System.out.println("Folder ID: " + folderId);

            // 2. Get every single file in this folder (recursively)
            List<File> files = getAllFiles(new File(folderPath));
            System.out.println("Found " + files.size() + " files"); // gets count

            // 3. Loop through results and save metadata to pete.db
            int filesIndexed = 0;
            for (File file : files) {
                insertFile(conn, file, folderId);
                filesIndexed++;

                // Every 100th file, print an update
                if (filesIndexed % 100 == 0) {
                    System.out.println("Indexed " + filesIndexed + " files...");
                }
            }

            // 4. Update total files column in folder table
            updateFolderCount(conn, folderId, filesIndexed);

            System.out.println("Scan complete! Indexed " + filesIndexed + " files");
            return filesIndexed;

        } catch (Exception e) {
            System.out.println("Error during scan:");
            e.printStackTrace();
            return 0;
        }
    }

    /** Prevents duplicating folders in DB and returns ID */
    private int insertFolder(Connection conn, String folderPath) throws Exception {
        // Check if folder exists before inserting
        String checkSql = "SELECT folder_id FROM folder WHERE path_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, folderPath);
            ResultSet rs = pstmt.executeQuery();
            // Don't want to scan C:/Photos again tomorrow and have 2 entries for the same folder.

            if (rs.next()) {
                return rs.getInt("folder_id");
            }
        }

        // If folder doesn't exist, create it
        String insertSql = "INSERT INTO folder (path_name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, folderPath);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new Exception("Failed to insert folder");
    }

    /** Recursively gets all files in a folder & subfolders. */
    private List<File> getAllFiles(File folder) {
        List<File> fileList = new ArrayList<>(); // Create list to hold files and get everything in folder & subfolder

        // Get all items in the folders
        File[] files = folder.listFiles();
        if (files == null) {
            return fileList;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(getAllFiles(file)); // <--- Method calls itself
                // If it's a folder, run getAllFiles on it too and continue until actual files are reached
            } else {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /** Inserts file metadata to file table. */
    private void insertFile(Connection conn, File file, int folderId) throws Exception {
        String sql = "INSERT INTO file (file_name, file_ext, file_size, date_modified, folder_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 1. Extract info from file
            String fileName = file.getName();
            String fileExt = getFileExtension(fileName);
            long fileSize = file.length();
            long dateModified = file.lastModified();

            // 2. Map data to the ? placeholders
            pstmt.setString(1, fileName);
            pstmt.setString(2, fileExt);
            pstmt.setLong(3, fileSize);
            pstmt.setLong(4, dateModified);
            pstmt.setInt(5, folderId);

            pstmt.executeUpdate();
        }
    }

    /** Updates the folder total files count (often times many folders will be scanned). */
    private void updateFolderCount(Connection conn, int folderId, int fileCount) throws Exception {
        String sql = "UPDATE folder SET total_files = ? WHERE folder_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fileCount);
            pstmt.setInt(2, folderId);
            pstmt.executeUpdate();
        }
    }

    /** Gets file extension from a filename. */
    // Move helper methods to the approporiate file.
    private String getFileExtension(String fileName) {
        // Find the last "." in filename and get everything from that position onwards
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot); // ".jpg" and the rest of the goodies (includes ".")
        }
        return ""; // If no extensions (yes its possible)
    }
}