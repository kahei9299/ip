package yap;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Controller for a dialog bubble consisting of text and an avatar image.
 *
 * <p>Uses the fx:root pattern (FXML loads into this HBox). Fields annotated with
 * {@code @FXML} are injected from {@code DialogBox.fxml}.</p>
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a dialog box by loading {@code DialogBox.fxml} and then setting the text and image.
     *
     * @param text Message to display.
     * @param img  Avatar image.
     */
    public DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // In SE-EDU, failing to load the resource is considered unrecoverable.
            throw new AssertionError("Failed to load DialogBox.fxml: " + e.getMessage(), e);
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Returns a right-aligned dialog box representing the user.
     *
     * @param text Message text.
     * @param img  User avatar image.
     * @return Configured dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Returns a left-aligned dialog box representing Yap (the bot).
     *
     * @param text Message text.
     * @param img  Bot avatar image.
     * @return Configured dialog box flipped to left alignment.
     */
    public static DialogBox getYapDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }

    /**
     * Flips the dialog box so the image is on the left and text on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }
}

