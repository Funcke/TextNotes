package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.sql.*;

public class SignUpController {
    @FXML TextField username;
    @FXML PasswordField password;
    @FXML TextField passwordVisible;
    @FXML PasswordField repeatPassword;
    @FXML TextField passwordRepeatVisible;
    @FXML Alert info;
    @FXML CheckBox showPassword;
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
            String pwordInput = "";

            while(rs.next()){
                if(rs.getString("username").contentEquals(username.getText())) {
                    this.unused = false;
                }
            }

            if(showPassword.isSelected()){
                if(this.unused && this.passwordVisible.getText().contentEquals(this.passwordRepeatVisible.getText())) {
                    pwordInput = this.passwordVisible.getText();
                }
            }
            else
            {
                if(this.unused && this.password.getText().contentEquals(this.repeatPassword.getText())) {
                    pwordInput = this.password.getText();
                }
            }

            if(this.unused && (!pwordInput.contentEquals(""))) {
                PreparedStatement pstmt = userDB.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)");
                pstmt.setString(1, username.getText());
                pstmt.setString(2, pwordInput);
                pstmt.execute();
                Window current = this.username.getScene().getWindow();
                current.hide();
            }
            else{
                info = new Alert(Alert.AlertType.INFORMATION);
                info.setHeaderText("Somethings wrong with your user:");
                info.setContentText("Username already taken or passwords don't match");
                info.show();
            }
        }
        catch(SQLException err) {
            System.err.println(err.getMessage());
        }
    }

    public void viewPW() {
        if(showPassword.isSelected()) {
            this.password.setVisible(false);
            this.passwordVisible.setVisible(true);
            this.passwordVisible.setText(password.getText());

            this.repeatPassword.setVisible(false);
            this.passwordRepeatVisible.setVisible(true);
            this.passwordRepeatVisible.setText(this.repeatPassword.getText());
        }
        else
        {
            this.passwordVisible.setVisible(false);
            this.password.setVisible(true);
            this.password.setText(this.passwordVisible.getText());

            this.passwordRepeatVisible.setVisible(false);
            this.repeatPassword.setVisible(true);
            this.repeatPassword.setText(this.passwordRepeatVisible.getText());
        }
    }

}
