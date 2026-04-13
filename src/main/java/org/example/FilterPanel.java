package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Filter panel that provides dropdowns for filtering files by type and size.
 */

public class FilterPanel extends HBox {
    // The dropdowns and callback
    private ComboBox<String> fileTypeComboBox;
    private ComboBox<String> fileSizeComboBox;
    private Runnable onFilterChange;

    /** Filter panel with type and size filters */
    public FilterPanel() {
        // add more css to hBox
        this.setSpacing(15);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filters:");

        // File type filter
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
        fileTypeComboBox.setOnAction(e -> notifyFilterChange());

        // File size filter
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
        fileTypeComboBox.setOnAction(e -> notifyFilterChange());

        this.getChildren().addAll(
                filterLabel, typeLabel, fileTypeComboBox, sizeLabel, fileSizeComboBox);
    }

    /** Sets callback to be called when a filter change happens */
    public void setOnFilterChange(Runnable callback) {
        this.onFilterChange = callback;
    }

    /** Notifies parent that filter was changed, called when I change dropdown */
    private void notifyFilterChange() {
        if (onFilterChange != null) {
            onFilterChange.run();
        }
    }
}
