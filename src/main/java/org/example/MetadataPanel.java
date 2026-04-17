package org.example;

import javafx.scene.control.Label;
import javafx.stage.Popup;

/**
 * Shows detailed file info when hovering for 3 seconds
 */

public class MetadataPanel {
    private Popup popup;

    private Label fileNameLabel;
    private Label fileSizeLabel;
    private Label resolutionLabel;
    private Label dateModifiedLabel;
    private Label fileTypeLabel;
    private Label filePathLabel;

    public MetadataPanel() {
        popup = new Popup();

    }

}
