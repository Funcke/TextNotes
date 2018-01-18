package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class LogInController {
    @FXML Label lbl_header;
    @FXML Label lbl_uName;
    @FXML Label lbl_pWord;
    @FXML Label lbl_footer;
    @FXML TextField txt_uName;
    @FXML PasswordField txt_pWord;
    @FXML Button cmd_submit;
    @FXML Button cmd_signUp;
    private Connection userDB;

    @FXML
    public void initialize() {
        this.lbl_header.setText("Log In");
        this.lbl_uName.setText("Username");
        this.lbl_pWord.setText("Password");
        this.connectToDB();
    }

    @FXML
    public void cmd_submit() {
        String password = txt_pWord.getText();
        String username = txt_uName.getText();
        boolean userValid = false;

        userValid = this.validUser(username, password);

        if (userValid) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("user"));
                bw.write(password + ";" + username);
                bw.close();
                this.open(username);
            } catch (IOException|SQLException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        else {
            System.err.println("Invalid login data");
            Alert info = new Alert(Alert.AlertType.ERROR);
            info.setHeaderText("Error!");
            info.setContentText("Invalid Log In Data");
            info.show();
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

    private void open(String name) throws IOException, SQLException {
        userDB.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Main.fxml"));
        Parent root = loader.load();
        Stage view = (Stage) cmd_signUp.getScene().getWindow();
        MainController controller = loader.getController();

        controller.init(name);
        view.setTitle("TextNotes");
        view.setScene(new Scene(root, 1125, 600));
        view.setResizable(false);
        view.sizeToScene();
        view.centerOnScreen();
        view.show();
    }

    private boolean connectToDB() {
        try {
            userDB = DriverManager.getConnection("jdbc:sqlite:posts.db");
            Statement stmt = userDB.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS users(\n"
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n"
                    + "username VARCHAR UNIQUE,\n"
                    + "password VARCHAR NOT NULL\n"
                    + ");");
            return true;
        } catch (SQLException err) {
            System.err.println(err.getMessage() + "111");
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
