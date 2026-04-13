package org.example;

/**
 * Static util methods for filtering
 */

public class FilterUtils {

    public static boolean matchesFileType(FileRecord file, String typeFilter) {
        // If all types, everything matches
        if (typeFilter == null || typeFilter.equals("All Types")) {
            return true;
        }

        String ext = file.getFileExt();
        if (ext == null || ext.isEmpty()) {
            return typeFilter.equals("Other");
        }

        ext = ext.toLowerCase();

        switch (typeFilter) { // figure out how to make this less annoying
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

            default:
                return true;
        }
    }

    public static boolean MatchesFileSize(FileRecord file, String sizeFilter) {
        // If all sizes, everything matches
        if (sizeFilter == null || sizeFilter.equals("All Sizes")) {
            return true;
        }

        // Parse formatted size back to bytes.
        // new method for parsing files to bytes

    }
    public static long parseFileSizetoBytes(String sizeString) {


    }

}
