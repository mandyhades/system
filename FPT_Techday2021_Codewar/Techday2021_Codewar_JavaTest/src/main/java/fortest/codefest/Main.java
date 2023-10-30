package fortest.codefest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadSocketController(primaryStage);
    }

    private void loadSocketController(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("socketlayout.fxml"));
        primaryStage.setTitle("ForTest - Infinity War");
        primaryStage.setScene(new Scene(root, 690, 650));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
