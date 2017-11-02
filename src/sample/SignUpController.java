package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.sql.*;

public class SignUpController {
    @FXML TextField username;
    @FXML TextField password;
    @FXML TextField repeatPassword;
    @FXML Label info;
    @FXML Button check;
    private Connection userDB;
    private boolean unused = true;

    public void setConnection(Connection conn){
        this.userDB = conn;
    }

    @FXML
    public void submit() {
        try {
            Statement stmt = userDB.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM users;");

            while(rs.next()){
                if(rs.getString("username").contentEquals(username.getText())) {
                    this.unused = false;
                }
            }

            if(this.unused && this.password.getText().contentEquals(this.repeatPassword.getText())) {
                PreparedStatement pstmt = userDB.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)");
                pstmt.setString(1, username.getText());
                pstmt.setString(2, password.getText());
                pstmt.execute();
                Window current = this.info.getScene().getWindow();
                current.hide();
            }
            else{
                info.setText("Username already taken or passwords don't match");
            }
        }
        catch(SQLException err) {
            System.err.println(err.getMessage());
        }
    }

}
