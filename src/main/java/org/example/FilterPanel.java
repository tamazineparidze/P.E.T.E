package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Filter panel that provides dropdown menus for type and size filters.
 */

public class FilterPanel extends HBox {

    // The dropdowns and callback, hold state of current filter selection and logic passed from parent
    private ComboBox<String> fileTypeComboBox;
    private ComboBox<String> fileSizeComboBox;
    private ComboBox<String> fileTimeComboBox;  /** FILE TIME FOR FUTURE */
    private Runnable onFilterChange;

    /** Filter panel with type and size filters. */
    public FilterPanel() {
        // 1. Make HBox parameters
        this.setSpacing(15);
        this.setPadding(new Insets(10, 12, 10, 12));
        this.setAlignment(Pos.CENTER_LEFT);

        // 2. Filter UI label
        Label filterLabel = new Label("Filters:");

        // 3. File type filter
        Label typeLabel = new Label("Type:");
        fileTypeComboBox = new ComboBox<>();
        fileTypeComboBox.getItems().addAll(
                "All Types",
                "Images",
                "Videos",
                "Audio",
                "Documents",
                "Other"
        );
        fileTypeComboBox.setValue("All Types");
        fileTypeComboBox.setPrefWidth(150);
        // When user picks a new type, triggers the notify method
        fileTypeComboBox.setOnAction(e -> notifyFilterChange());

        // 4. File size filter
        Label sizeLabel = new Label("Size:");
        fileSizeComboBox = new ComboBox<>();
        fileSizeComboBox.getItems().addAll(
                "All Sizes",
                "< 1 MB",
                "1 - 10 MB",
                "10 - 100 MB",
                "> 100 MB"
        );
        fileSizeComboBox.setValue("All Sizes");
        fileSizeComboBox.setPrefWidth(150);
        fileSizeComboBox.setOnAction(e -> notifyFilterChange()); // Similar to previous trigger method

        // 000 Extra Add later
        Label timeLabel = new Label("Time:"); /** FILE TIME FOR FUTURE */


        this.getChildren().addAll(
                filterLabel, typeLabel, fileTypeComboBox, sizeLabel, fileSizeComboBox);
    }

    /** Sets callback to be called when a filter change happens. */
    public void setOnFilterChange(Runnable callback) {
        this.onFilterChange = callback;
    }

    /** Notifies parent that filter was changed, called when dropdown is changed. */
    private void notifyFilterChange() {
        if (onFilterChange != null) {
            onFilterChange.run();
        }
    }

    /** Gets the selected file type filter (Video). */
    public String getSelectedFileType() {
        return fileTypeComboBox.getValue();
    }

    /** Gets the selected file size filter (10 - 100 MB). */
    public String getSelectedFileSize() {
        return fileSizeComboBox.getValue();
    }
}
