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

public class FileIndexer {

    private static final String DB_URL = "jdbc:sqlite:pete.db"; // Pete will repeat the same database

    /**
     * scans a folder and indexes all files to pete.db
     */
    public int scanFolder(String folderPath) {

        System.out.println("Starting scan of: " + folderPath);

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // opens connection to database & closes when done

            // 1: add folder into the folder table / returns folder id
            int folderId = insertFolder(conn, folderPath);
            System.out.println("Folder ID: " + folderId);

            // 2: get every single file in this folder (recursively)
            List<File> files = getAllFiles(new File(folderPath));
            System.out.println("Found " + files.size() + " files"); // gets count

            // 3: for each file in the list (of files) insert into database with folder id
            int filesIndexed = 0;
            for (File file : files) { // For each loop, more complext than tradition for but same thing
                insertFile(conn, file, folderId);
                filesIndexed++;

                // every 100th file, prints an update
                if (filesIndexed % 100 == 0) {
                    System.out.println("Indexed " + filesIndexed + " files...");
                }
            }

            // 4: update total_files column in folder table and display result
            updateFolderCount(conn, folderId, filesIndexed);

            System.out.println("Scan complete! Indexed " + filesIndexed + " files");
            return filesIndexed; // return for ui to display

        } catch (Exception e) { //if error, print details and return 0 for no files indexed
            System.out.println("Error during scan:");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * adds folder to database and returns ID
     */
    private int insertFolder(Connection conn, String folderPath) throws Exception { //reuses connections paramtere
        // check if folder exists before inserting
        String checkSql = "SELECT folder_id FROM folder WHERE path_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) { // pstmt = prepared statement (helps avoid deleting folder)
            // replaces the ? with folderPath & pstmt deals with the special characters
            pstmt.setString(1, folderPath);
            ResultSet rs = pstmt.executeQuery();
            // don't want to scan C:/Photos again tomorrow and have 2 entries for the same folder.

            if (rs.next()) { //
                // folder already exists, return ID
                return rs.getInt("folder_id"); // you dont want to scan photos today give it id1 and scan tomorrow
                // give it id 2. The above checks and links to same folder (if there)
            }
        }

        // if folder doesn't exist, inser it
        String insertSql = "INSERT INTO folder (path_name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) { // after insert give auto generated id
            pstmt.setString(1, folderPath);
            pstmt.executeUpdate();
            // sql inserts new folder & return new ID

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        // insert and no id return? or just failed to insert
        throw new Exception("Failed to insert folder");
    }

    /**
     * recursively gets all files in a folder & subfolders
     * recursively is a big word, google it (i had to)
     */
    private List<File> getAllFiles(File folder) {
        List<File> fileList = new ArrayList<>(); // create list to hold files and get everything in folder & subfolder

        // get all items in the folders
        File[] files = folder.listFiles();
        if (files == null) {
            return fileList; // folder is empty or something not good
        }

        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(getAllFiles(file)); // <--- method calls itself
                // so basically it will take a folder with files (and other folders) add the files to filelist and
                // when it comes across a folder it will recall the method and do what it just did for the first folder
                // in the next folder untill all folder are scanned and only files have been added to filelist
            } else {
                fileList.add(file);
            }
        }
        return fileList; // return complete list of ALL files
    }

    /**
     * inserts a file into the database
     */
    private void insertFile(Connection conn, File file, int folderId) throws Exception {
        String sql = "INSERT INTO file (file_name, file_ext, file_size, date_modified, folder_id) " +
                "VALUES (?, ?, ?, ?, ?)"; // placeholders of data being inserted (i'll probably add more)

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // extract info from file
            String fileName = file.getName(); // get the filename - fred
            String fileExt = getFileExtension(fileName); // get extension - .jpg
            long fileSize = file.length(); // can handle 20gb fred jpegs
            long dateModified = file.lastModified(); // ms since 1970 or something

            // replaced the ?'s from before with data we just got above (folder id is automatic)
            pstmt.setString(1, fileName);
            pstmt.setString(2, fileExt);
            pstmt.setLong(3, fileSize);
            pstmt.setLong(4, dateModified);
            pstmt.setInt(5, folderId);

            pstmt.executeUpdate(); // data is in pete.db now
        }
    }

    /**
     * updates the folder total_files count (often times many folders will be scanned)
     */
    private void updateFolderCount(Connection conn, int folderId, int fileCount) throws Exception {
        // after scan, update the folder table to show # files scanned
        // if folder id 1 had 0 total files it will have ### files now

        String sql = "UPDATE folder SET total_files = ? WHERE folder_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fileCount);
            pstmt.setInt(2, folderId);
            pstmt.executeUpdate();
        }
    }

    /**
     * gets file extension from a filename
     */
    private String getFileExtension(String fileName) {
        // find the last "." in filename and get everything from that position onwards
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot); // ".jpg" and the rest of the goodies (includes ".")
        }
        return ""; // if no extensions (yes its possible)
    }
}

/**
 * For me: comment to remember what your code means and how it executes
 * if intelliJ autofills, figure out what it means, dont trust it blindly
 * know difference between prepared and normal statement and where to use them
 */