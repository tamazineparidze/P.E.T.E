// creates database schema and runs at the beginning

package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {

    // file location as a private static final
    private static final String DB_URL = "jdbc:sqlite:pete.db"; // proper protocol, database, and filename

    /**
     * main method which calls CreateTables
     */
    public static void main(String[] args) {
        System.out.println("Setting up P.E.T.E. database...\n");
        createTables();
    }

    public static void createTables() {
        // connects to pete.db
        try (Connection conn = DriverManager.getConnection(DB_URL); // opens connection to pete.db
             Statement stmt = conn.createStatement()) { // creates statement to execute sql
            // when try block ends automatically closes stmt and conn

            // create folde table if not there with following parameters
            String createFolderTable = """
                CREATE TABLE IF NOT EXISTS folder ( 
                    folder_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    path_name TEXT NOT NULL UNIQUE,
                    date_indexed DATETIME DEFAULT CURRENT_TIMESTAMP,
                    total_files INTEGER DEFAULT 0
                )
                """;

            stmt.execute(createFolderTable); // sends sql to database
            System.out.println("Folder table created");

            // same thing for file table
            // auto increment, filename required, extension (can be null), filesize in bytes...
            String createFileTable = """
                CREATE TABLE IF NOT EXISTS file (
                    file_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    file_name TEXT NOT NULL,
                    file_ext TEXT,
                    file_size INTEGER,
                    resolution TEXT,
                    date_modified DATETIME,
                    folder_id INTEGER NOT NULL,
                    FOREIGN KEY (folder_id) REFERENCES folder(folder_id)
                )
                """;
            // foreign key constraint enforces data integrity because every file should point to a folder

            stmt.execute(createFileTable);
            System.out.println("File table created");

            System.out.println("\nDatabase setup complete!"); // folder and file are done

        } catch (Exception e) { // if any error in try block, run this
            System.out.println("Error setting up database:");
            e.printStackTrace();
        }
    }
}

/**
 * For me: remember to give lines the apporporiate properties.
 * final, static, identifiers, constraints
 * if stuck and found a solution that you did not think of look at the reference
 * and understand WHY it works and how to not get stuck in a similar situation again.
 */