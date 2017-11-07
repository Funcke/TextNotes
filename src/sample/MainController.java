package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MainController {
    @FXML MenuItem create;
    @FXML MenuItem save;
    @FXML TextArea input;
    @FXML Label postText;
    @FXML ListView notes;

    protected String name = "";
    protected Connection notesDB = null;
    protected ArrayList<Note> noteList = new ArrayList<>();
    protected boolean editMode = false;
    protected int id;

    public void init(String nm) {
        this.name = nm;
        this.postText.setText("Note sth. " + this.name + "!");
        initializeForm();
    }

    //initializes From data
    public void initializeForm(){
        notes.getItems().clear(); //clears all notes   from the view
        //checks if connection to db exists
        if(notesDB == null) {
            try {
                notesDB = DriverManager.getConnection("jdbc:sqlite:posts.db");

                System.out.println("Connection succeeded");
            } catch (SQLException err) {
                System.err.println(err.getLocalizedMessage());
            }
            try {
                String sql = "CREATE TABLE IF NOT EXISTS notes (\n"
                        + "	id integer PRIMARY KEY,\n"
                        + "	content text NOT NULL\n,"
                        + " owner text NOT NULL\n"
                        + ");";
                Statement stmt = notesDB.createStatement();

                stmt.execute(sql);
            } catch (SQLException err) {
                System.err.println(err.getMessage() + "49");
            }
        }
        //gets all notes  from db
        try{
            PreparedStatement stmt = notesDB.prepareStatement("SELECT id, content FROM notes WHERE owner= ? ");
            stmt.setString(1, name);
            ResultSet res = stmt.executeQuery();

            noteList.clear();

            while(res.next()){
                noteList.add(new Note(res.getInt("id"), res.getString("content")));
            }

            for (Note n: noteList) {
                Label note = new Label(n.getContent().split("\n")[0]);

                note.setId("post");
                note.setOnMouseClicked((mouseEvent) -> {
                    //event on left click
                    if (mouseEvent.getButton() == MouseButton.PRIMARY){
                        input.setVisible(true);
                        input.setText(n.getContent());
                        create.setVisible(false);
                        save.setVisible(true);

                        this.editMode = true;
                        this.id = n.getId();
                    }else //event on right click
                        if(mouseEvent.getButton() == MouseButton.SECONDARY) {
                            if (!this.editMode){
                                StringBuilder sb = new StringBuilder();

                                try {
                                    PreparedStatement state = notesDB.prepareStatement("DELETE FROM notes WHERE id = ? AND owner= ?");

                                    for (Note nin : noteList) {
                                        if (nin.getContent() == n.getContent()) {
                                            sb.append(n.getId());
                                            state.setString(1, sb.toString());
                                            state.setString(2, this.name);
                                            state.execute();
                                        }
                                    }
                                } catch (SQLException err) {
                                    System.out.println(err.getMessage());
                                }
                                this.initializeForm();
                            }
                        }
                });
                notes.getItems().add(note); //add to listView
            }
        }
        catch(SQLException err){
            System.err.println(err.getMessage());
        }
    }

    @FXML
    public void save() {
        if(!this.input.getText().contentEquals("")){
            try {
                if (this.editMode) {
                    PreparedStatement stmt = notesDB.prepareStatement("UPDATE notes SET content = ? WHERE id = ?;");

                    stmt.setString(1, input.getText());
                    stmt.setInt(2, this.id);
                    stmt.execute();
                    this.editMode = false;
                } else {
                    PreparedStatement stmt = notesDB.prepareStatement("INSERT INTO notes (content, owner) VALUES(?, ?)");
                    stmt.setString(1, input.getText());
                    stmt.setString(2, this.name);
                    stmt.execute();
                }
            } catch (SQLException err) {
                System.err.println(err.getMessage());
            }
        }
        this.input.setText("");
        this.initializeForm();
        this.stopInput();
    }

    /*
    @FXML
    public void setMode() {
        if(this.nightMode.isSelected()) {
            nightMode.getScene().getStylesheets().clear();
            nightMode.getScene().getStylesheets().add(getClass().getResource("styles/main.css").toExternalForm());
        }
        else{
            nightMode.getScene().getStylesheets().clear();
            nightMode.getScene().getStylesheets().add(getClass().getResource("styles/mainBright.css").toExternalForm());
        }
    }*/

    @FXML
    public void logOff() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/LogIn.fxml"));
            Parent root = loader.load();
            Stage view = (Stage) notes.getScene().getWindow();

            view.getIcons().add(new Image("file:ic_format_align_right_black_48dp.png"));
            view.setTitle("Log In");
            view.setScene(new Scene(root, 300, 275));
            view.show();
        }
        catch(IOException err) {
            System.err.println("IOException");
        }
    }

    public void create() {
        this.create.setVisible(false);
        this.save.setVisible(true);
        this.input.setVisible(true);
        this.input.requestFocus();
    }

    private void stopInput() {
        this.save.setVisible(false);
        this.create.setVisible(true);
        this.input.setVisible(false);
    }
}
