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
    private PauseTransition hoverTimer;

    private javafx.stage.Window window;
    private double posX;
    private double posY;

    public MetadataPanel() {
        // Create popup, container, label, and hover
        popup = new Popup();

        VBox container = new VBox();
        container.setPadding(new Insets(10, 10, 10, 10));
        container.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px;");

        messageLabel = new Label("Popup");

        container.getChildren().add(messageLabel);
        popup.getContent().addAll(container);

        hoverTimer = new PauseTransition(Duration.seconds(1));
        hoverTimer.setOnFinished(e -> showPopup());

    }

    /** Starts the 1 second time and stores position. */
    public void startTimer(javafx.stage.Window window, double x, double y) {
        this.window = window;
        this.posX = x;
        this.posY = y;

        hoverTimer.playFromStart();
    }

    /** Cancels the timer. */
    public void cancelTimer() {
        hoverTimer.stop();
        hide();
    }

    /** Shows popup after the timer finishes. */
    private void showPopup() {
        popup.show(window, posX, posY);
    }

    /** Sets what text to show. */
    public void setText(String text) {
        messageLabel.setText(text);
    }

    /** Hides the popup. */
    public void hide() {
        popup.hide();
    }
}
