// MainWindow.java
package yap;

import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import yap.core.GuiYapAdapter;

public class MainWindow extends AnchorPane {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;

    private GuiYapAdapter engine;
    private boolean nameProvided = false;

    private Image userImage;
    private Image botImage;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        // Load images here (after classpath is ready) and fail clearly if missing.
        URL userUrl = getClass().getResource("/images/User.png");
        URL botUrl  = getClass().getResource("/images/Yap.png");
        userImage = new Image(Objects.requireNonNull(userUrl, "Missing /images/User.png").toExternalForm());
        botImage  = new Image(Objects.requireNonNull(botUrl,  "Missing /images/Yap.png").toExternalForm());
    }

    /** Injects the adapter and shows the CLI-equivalent greeting. */
    public void setEngine(GuiYapAdapter adapter) {
        this.engine = adapter;
        dialogContainer.getChildren().add(
                DialogBox.getYapDialog(engine.getGreetingAndPrompt(), botImage));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));

        String reply = !nameProvided
                ? engine.setUserName(input.trim())
                : engine.handle(input);
        nameProvided = true;

        dialogContainer.getChildren().add(DialogBox.getYapDialog(reply, botImage));
        userInput.clear();

        if (engine.isExit()) {
            Stage s = (Stage) dialogContainer.getScene().getWindow();
            s.close();
        }
    }
}
