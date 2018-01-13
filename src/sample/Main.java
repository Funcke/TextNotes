package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/LogIn.fxml"));
        primaryStage = new Stage();
        primaryStage.getIcons().add(new Image("file:ic_format_align_right_black_48dp.png"));
        primaryStage.setTitle("Log In");
        primaryStage.setScene(new Scene(root, 300, 325));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
