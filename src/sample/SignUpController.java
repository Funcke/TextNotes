package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.sql.*;

/**
 * @author Jonas Funcke
 *
 * Controller for the Sign Up Window
 */
public class SignUpController {
    @FXML TextField txt_username;
    @FXML PasswordField pw_password;
    @FXML TextField txt_passwordVisible;
    @FXML PasswordField pw_repeatPassword;
    @FXML TextField txt_passwordRepeatVisible;
    @FXML Alert info;
    @FXML CheckBox cb_showPassword;
    private Connection userDB;
    private boolean unused = true;

    /**
     * Setter for the connection to the DB
     * @param conn - Database Connection
     */
    public void setConnection(Connection conn){
        this.userDB = conn;
    }

    /**
     * onClick action for the submiting of the form.
     * Registers the user in the database and closes itself.
     */
    @FXML
    public void submit() {
        try {
            Statement stmt = userDB.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM users;");
            String pwordInput = "";

            while(rs.next()){
                if(rs.getString("username").contentEquals(txt_username.getText())) {
                    this.unused = false;
                }
            }

            if(cb_showPassword.isSelected()){
                if(this.unused && this.txt_passwordVisible.getText().contentEquals(this.txt_passwordRepeatVisible.getText())) {
                    pwordInput = this.txt_passwordVisible.getText();
                }
            }
            else
            {
                if(this.unused && this.pw_password.getText().contentEquals(this.pw_repeatPassword.getText())) {
                    pwordInput = this.pw_password.getText();
                }
            }

            if(this.unused && (!pwordInput.contentEquals(""))) {
                PreparedStatement pstmt = userDB.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)");
                pstmt.setString(1, txt_username.getText());
                pstmt.setString(2, pwordInput);
                pstmt.execute();
                Window current = this.txt_username.getScene().getWindow();
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

    /**
     * onChange operation for checkbox.
     * Shows or hides the password fields and shows or hides the textFields
     */
    @FXML
    public void viewPW() {
        if(cb_showPassword.isSelected()) {
            this.pw_password.setVisible(false);
            this.txt_passwordVisible.setVisible(true);
            this.txt_passwordVisible.setText(pw_password.getText());

            this.pw_repeatPassword.setVisible(false);
            this.txt_passwordRepeatVisible.setVisible(true);
            this.txt_passwordRepeatVisible.setText(this.pw_repeatPassword.getText());
        }
        else
        {
            this.txt_passwordVisible.setVisible(false);
            this.pw_password.setVisible(true);
            this.pw_password.setText(this.txt_passwordVisible.getText());

            this.txt_passwordRepeatVisible.setVisible(false);
            this.pw_repeatPassword.setVisible(true);
            this.pw_repeatPassword.setText(this.txt_passwordRepeatVisible.getText());
        }
    }

}
