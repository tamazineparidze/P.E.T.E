package org.example;

import java.time.LocalDate;

/**
 * Static util methods for filtering
 */

public class FilterUtils {

    /** Checks if file matches selected type filter. */
    public static boolean matchesFileType(FileRecord file, String typeFilter) {
        // 1. If all types is picked or no filter is set, everything matches
        if (typeFilter == null || typeFilter.equals("All Types")) {
            return true;
        }

        // 2. Get the extension and label files for no extension
        String ext = file.getFileExt();
        if (ext == null || ext.isEmpty()) {
            return typeFilter.equals("Other"); // The no extensions
        }

        ext = ext.toLowerCase(); // Make lowercase to filter regardless of upper/lowercase

        // 3. Categorization - compare the extension to known file extensions for that type of file
        switch (typeFilter) { /** make compact */
            case "Images":
                return ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") ||
                        ext.equals(".gif") || ext.equals(".heic") || ext.equals(".webp") ||
                        ext.equals(".raw") || ext.equals(".nef") || ext.equals(".raf");
            case "Videos":
                return ext.equals(".mp4") || ext.equals(".avi") || ext.equals(".mkv") ||
                        ext.equals(".mov") || ext.equals(".wmv") || ext.equals(".flv") ||
                        ext.equals(".webm");
            case "Audio":
                return ext.equals(".mp3") || ext.equals(".wav") || ext.equals(".flac") ||
                        ext.equals(".aac") || ext.equals(".ogg") || ext.equals(".m4a");
            case "Documents":
                return ext.equals(".pdf") || ext.equals(".doc") || ext.equals(".docx") ||
                        ext.equals(".xls") || ext.equals(".xlsx") || ext.equals(".ppt") ||
                        ext.equals(".pptx") || ext.equals(".txt");
            case "Other":
                // Everything that doesn't match categories
                return !matchesFileType(file, "Images") &&
                        !matchesFileType(file, "Videos") &&
                        !matchesFileType(file, "Audio") &&
                        !matchesFileType(file, "Documents");

            default: // If filter type that's not recognized is passed, default to showing the file
                return true;
        }
    }

    /** Checks if file matches selected size filter. */
    public static boolean matchesFileSize(FileRecord file, String sizeFilter) {
        // If all sizes, everything matches
        if (sizeFilter == null || sizeFilter.equals("All Sizes")) {
            return true;
        }

        // 1. Parse formatted size back to bytes.
        String sizeString = file.getFileSize();
        long bytes = parseFileSizeToBytes(sizeString);

        // 2. Make range categories to define boundaries between sizes
        switch(sizeFilter) {
            case "< 1 MB": // For my old digicam
                return bytes < 1024 * 1024;
            case "1 - 10 MB": // Usual smartphone
                return bytes >= 1024 * 1024 && bytes < 10 * 1024 * 1024;
            case "10 - 100 MB": // Mirorless camera
                return bytes >= 10 * 1024 * 1024 && bytes < 100 * 1024 * 1024;
            case "> 100 MB": // High MP raw photos
                return bytes > 100 * 1024 * 1024;
                default:
                    return true;
        }

    }

    /** Converts formatted size to bytes. */
    public static long parseFileSizeToBytes(String sizeString) {
        try {
            // 1. Split into number and unit (800 and Kilobytes)
            String[] parts = sizeString.split(" ");
            if (parts.length != 2) {
                return 0;
            }

            double value = Double.parseDouble(parts[0]);
            String unit = parts[1];

            // 2. Apply the multiplier based on size
            switch (unit) { /** make nicer looking */
                case "B":
                    return (long) value;
                case "KB":
                    return (long) value * 1024;
                case "MB":
                    return (long) value * 1024 * 1024;
                case "GB":
                    return (long) value * 1024 * 1024 * 1024;
                default:
                    return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean matchesDateRange(FileRecord file, LocalDate fromDate, LocalDate toDate) {
        // 1. If both dates are null everything matches
        if (fromDate == null && toDate == null) {
            return true;
        }

        // 2. Get file's modification date and convert it
        LocalDate fileDate = parseFileDate(file.getDateModified());

        // 3. Check if file is within the bounds of the date
        if (fileDate == null) { // Can't parse date, exclude from filter
            return false;
        }

        if (fromDate != null && fileDate.isBefore(fromDate)) { // Check from date (file is before start date)
            return false;
        }

        if (toDate != null && fileDate.isAfter(toDate)) { // Check to date (file is after end date)
            return false;
        }

        return true;
    }

    public static LocalDate parseFileDate(String dateString) {
        try {
            // Filerecord has YYYY-MM-DD HH:MM:SS we only need the date
            if (dateString == null || dateString.length() < 10) {
                return null;
            }

            String datePart = dateString.substring(0, 10); // Extract just the date
            return LocalDate.parse(datePart);

    } catch (Exception e) {
            System.out.println("Could not parse date: " + dateString);
            return null;
        }

    }
}
