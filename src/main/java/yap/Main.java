package yap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import yap.core.GuiYapAdapter;

/**
 * Entry point for the JavaFX app.
 * Loads {@code MainWindow.fxml}, injects the CLI-bridged engine, and shows the stage.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the root defined in MainWindow.fxml (controller is declared in FXML)
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();

        // Get controller and inject the engine that guarantees CLI-identical behaviour
        MainWindow controller = fxmlLoader.getController();
        controller.setEngine(new GuiYapAdapter("data/tasks.txt"));

        // Create and show the scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Yap");
        stage.show();
    }

    /**
     * Standard launcher when running without a separate module launcher.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
