package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root;
        boolean userValid = false;
        File file = new File("user");

        if(file.exists()){
            BufferedReader br = new BufferedReader(new FileReader("user"));
            String[] data = br.readLine().split(";");
            br.close();
            try {
                Connection userDB = DriverManager.getConnection("jdbc:sqlite:posts.db");
                PreparedStatement stmt = userDB.prepareStatement("SELECT username, password FROM users WHERE username = ?;");
                stmt.setString(1, data[0]);
                ResultSet rs = stmt.executeQuery();

                while(rs.next()){
                    if(rs.getString("username").contentEquals(data[0]) && rs.getString("password").contentEquals(data[1])){
                        userDB.close();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));
                        root = loader.load();
                        MainController controller = loader.getController();

                        controller.init(data[0]);

                        primaryStage.setScene(new Scene(root, 1125, 600));
                        userValid = true;
                        break;
                    }
                }
            } catch (SQLException err) {
                System.err.println(err.getMessage());
            }
        }

        if(!userValid)
        {
            root = FXMLLoader.load(getClass().getResource("views/LogIn.fxml"));
            primaryStage = new Stage();
            primaryStage.setScene(new Scene(root, 350, 375));

            if(file.exists())
                file.delete();
        }

        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("file:ic_format_align_right_black_48dp.png"));
        primaryStage.setTitle("TextNotes");
        primaryStage.centerOnScreen();
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
