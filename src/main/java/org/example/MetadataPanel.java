package org.example;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * Shows detailed file info when hovering for 3 seconds
 */

public class MetadataPanel {
    private Popup popup;
    private Label messageLabel;

    public MetadataPanel() {

        popup = new Popup();

        VBox container = new VBox();
        container.setPadding(new Insets(10, 10, 10, 10));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px;");

        messageLabel = new Label("Popup");

        container.getChildren().add(messageLabel);

        popup.getContent().addAll(container);

    }

    public void setText(String text) {
        messageLabel.setText(text);
    }

    public void show(javafx.stage.Window window, double x, double y) {
        popup.show(window, x, y);
    }

    public void hide() {
        popup.hide();
    }
}
