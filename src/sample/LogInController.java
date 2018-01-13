package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LogInController {
    @FXML Label header;
    @FXML Label uNameHead;
    @FXML Label pWordHead;
    @FXML Label footer;
    @FXML TextField uName;
    @FXML PasswordField pWord;
    @FXML Button submit;
    @FXML Button signUp;
    private Connection userDB;

    @FXML
    public void initialize() {
        this.header.setText("Log In");
        this.uNameHead.setText("Username");
        this.pWordHead.setText("Password");
        this.connectToDB();
    }

    @FXML
    public void cmd_submit() {
        String password = pWord.getText();
        String username = uName.getText();
        boolean userValid = false;

        userValid = this.validUser(username, password);

        if (userValid) {
            try {
                this.open(username);
            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        else {
            System.err.println("Invalid login data");
        }
    }

    @FXML
    public void cmd_signUp(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/SignUp.fxml"));
            Parent root = loader.load();
            Stage view = new Stage();

            view.getIcons().add(new Image("file:ic_format_align_right_black_48dp.png"));
            view.setTitle("New User");
            view.setScene(new Scene(root, 275, 350));
            view.show();

            SignUpController sn = loader.getController();
            sn.setConnection(userDB);
        }
        catch(Exception err ) {
            System.err.println(err.getMessage());
        }
    }

    @FXML
    public void cmd_discard() {
        Platform.exit();
    }

    private void open(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));
        Parent root = loader.load();
        Stage view = (Stage) signUp.getScene().getWindow();
        MainController controller = loader.getController();

        controller.init(name);
        view.setTitle("TextNotes");
        view.setScene(new Scene(root, 750, 400));
        view.setResizable(false);
        view.sizeToScene();
        view.show();
    }

    private boolean connectToDB() {
        try {
            userDB = DriverManager.getConnection("jdbc:sqlite:user.db");
            Statement stmt = userDB.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS users(\n"
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                    + "username VARCHAR UNIQUE,\n"
                    + "password VARCHAR NOT NULL\n"
                    + ");");
            return true;
        } catch (SQLException err) {
            System.err.println(err.getMessage());
        }
        return false;
    }

    private boolean validUser(String username, String password) {
        try {
            Statement stmt = userDB.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, password FROM users;");

            while(rs.next()){
                if(rs.getString("username").contentEquals(username) && rs.getString("password").contentEquals(password))
                    return true;
            }
        } catch (SQLException err) {
            System.err.println(err.getMessage());
        }
        return false;
    }
}
