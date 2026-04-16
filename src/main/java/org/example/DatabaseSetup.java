package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Initializes SQLite database and defines tables/columns.
 */

public class DatabaseSetup {
    private static final String DB_URL = "jdbc:sqlite:pete.db"; // Proper protocol and filename

    public static void main(String[] args) {
        System.out.println("Setting up P.E.T.E. database...\n");
        createTables();
    }

    /** Executes Sql commands to build folder and file tables. */
    public static void createTables() {
        // Opens connection to pete.db and makes it if it doesn't exist
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // 1. Folder table, stores the directories that are scanned
            String createFolderTable = """
                CREATE TABLE IF NOT EXISTS folder ( 
                    folder_id INTEGER PRIMARY KEY AUTOINCREMENT,            -- Unique ID for every folder
                    path_name TEXT NOT NULL UNIQUE,                         -- Path string
                    date_indexed DATETIME DEFAULT CURRENT_TIMESTAMP,        -- Current time
                    total_files INTEGER DEFAULT 0                           -- Tracks # of files
                )
                """;

            stmt.execute(createFolderTable);
            System.out.println("Folder table created");

            // 2. File table, stores metadata for every file
            String createFileTable = """
                CREATE TABLE IF NOT EXISTS file (
                    file_id INTEGER PRIMARY KEY AUTOINCREMENT,              -- Unique ID for file
                    file_name TEXT NOT NULL,                                -- 'fred.jpg'
                    file_ext TEXT,                                          -- '.jpg'
                    file_size INTEGER,                                      -- Size (in bytes)
                    resolution TEXT,                                        -- Placeholder for image data
                    date_modified DATETIME,                                 -- Last modified date
                    folder_id INTEGER NOT NULL,                             -- ID of folder this file is in
                    FOREIGN KEY (folder_id) REFERENCES folder(folder_id)
                )
                """;

            stmt.execute(createFileTable);
            System.out.println("File table created");

            System.out.println("\nDatabase setup complete!");

        } catch (Exception e) {
            System.out.println("Error setting up database:");
            e.printStackTrace();
        }
    }
}