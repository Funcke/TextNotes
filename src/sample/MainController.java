package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.exceptions.NoNotebookSelectedException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

/*
 * @author Jonas Funcke
 * @use Controller for the Main Window
 */
public class MainController {
    @FXML MenuItem mi_create;
    @FXML MenuItem mi_save;
    @FXML TextArea txt_input;
    @FXML Label lbl_postText;
    @FXML Label lbl_createdDate;
    @FXML ListView lv_notes;
    @FXML TextField txt_newBook;
    @FXML GridPane gp_notebooks;

    private String name = "";
    private String notebook = "";
    private ToggleGroup noteBooks;
    private Connection notesDB = null;
    private ArrayList<Note> noteList = new ArrayList<>();
    private boolean editMode = false;
    private int id;

    /*
     * @use Initializes the Controller and is called after instantiation.
     *
     * @param takes the name of the user as parameter
     * @return none
     */

    public void init(String nm) {
        this.name = nm;
        this.lbl_postText.setText("Note sth. " + this.name + "!");
        this.noteBooks = new ToggleGroup();
        initializeForm();
    }

    /*
     * @use Initializes the Fields for the TextNotes and the Notebooks associated
     * with the user.
     * First check for correct connection to the database and if it's the case,
     * initialization of the needed tables.
     * Calls the methods initializeNotes and initializeNotebooks for
     * initializing the components.
     *
     * @param none
     * @return none
     */
    private void initializeForm(){
        //checks if connection to db exists
        if(notesDB == null) {
            try {
                notesDB = DriverManager.getConnection("jdbc:sqlite:posts.db");

                System.out.println("Connection succeeded");
            } catch (SQLException err) {
                System.err.println(err.getLocalizedMessage());
            }
            try {
                Statement stmt = notesDB.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS notebooks(\n"
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "owner VARCHAR NOT NULL,\n"
                        + "name VARCHAR NOT NULL,"
                        + "FOREIGN KEY(owner) REFERENCES user(name));");
                stmt.execute("CREATE TABLE IF NOT EXISTS notes (\n"
                        + " id integer PRIMARY KEY,\n"
                        + "content text NOT NULL,"
                        + " owner VARCHAR NOT NULL,"
                        + "notebook VARCHAR NOT NULL,"
                        + "created_at VARCHAR NOT NULL,"
                        + "FOREIGN KEY(owner) REFERENCES user(username),"
                        + "FOREIGN KEY (notebook) REFERENCES notebooks(name)"
                        + ");");
            } catch (SQLException err) {
                System.err.println(err.getMessage() + "88");
            }
        }
        this.initializeNotes();
        this.initializeNotebooks();
    }

    /*
     * Initializes the Notes view of the Window and retrieves all note objects
     * associated with the user from the notes table.
     *
     * @param none
     * @return none
     * @view Notes
     */
    private void initializeNotes() {
        lv_notes.getItems().clear(); //clears all notes   from the view
        try {
            PreparedStatement stmt;
            if(this.notebook.equals("")) {
                stmt = notesDB.prepareStatement("SELECT id, content, created_at  FROM notes WHERE owner= ? ");
            } else{
                stmt = notesDB.prepareStatement("SELECT id, content, created_at  FROM notes WHERE owner= ? AND notebook = ?");
                stmt.setString(2, this.notebook);
            }

            stmt.setString(1, name);
            ResultSet res = stmt.executeQuery();

            noteList.clear();

            while (res.next()) {
                noteList.add(new Note(res.getInt("id"), res.getString("content"), res.getString("created_at")));
            }

            noteList.sort(new Comparator<Note>() {
                @Override
                public int compare(Note first, Note second) {
                    return first.compareTo(second);
                }
            });

            for (Note n : noteList) {
                Label note = new Label(n.getContent().split("\n")[0]);

                note.setId("post");
                note.setOnMouseClicked((mouseEvent) -> {
                    //event on left click
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        txt_input.setVisible(true);
                        txt_input.setText(n.getContent());
                        lbl_createdDate.setText(n.getCreation());
                        mi_create.setVisible(false);
                        mi_save.setVisible(true);

                        this.editMode = true;
                        this.id = n.getId();
                    } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            if (!this.editMode) {
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
                lv_notes.getItems().add(note); //add to listView
            }
        } catch(SQLException err) {
            System.err.println(err.getMessage());
        }
    }


    /*
     * Initializes the Notebooks View and retrieves all Notebook objects from the DB
     * that are associated with the user.
     *
     * @param none
     * @return none
     * @view Notebooks
     */
    private void initializeNotebooks() {
        gp_notebooks.getChildren().clear();
        try{
            PreparedStatement stmt = notesDB.prepareStatement("SELECT name FROM notebooks WHERE owner = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            int row = 0;
            while(rs.next()) {
                RadioButton newBook = new RadioButton(rs.getString(1));
                newBook.setId(rs.getString(1));
                newBook.setOnAction((event) -> {
                    RadioButton source = (RadioButton)event.getSource();
                    this.notebook =  source.getId();
                    this.initializeNotes();
                });
                newBook.setToggleGroup(this.noteBooks);
                gp_notebooks.add(newBook, 0, row);
                row++;
            }
        }catch(SQLException err) {
            System.err.println(err.getMessage());
        }
    }

    private void stopInput() {
        this.mi_save.setVisible(false);
        this.mi_create.setVisible(true);
        this.txt_input.setVisible(false);
    }

    /*
     * Retrieves all Input from the Textarea, creates a new Note object,
     * saves it to the database and calls initializeNotes.
     *
     * @param none
     * @return none
     * @view none
     */
    @FXML
    public void cmd_save() {
        if(!this.txt_input.getText().contentEquals("") ){
            try {

                if (this.editMode) {
                    PreparedStatement stmt = notesDB.prepareStatement("UPDATE notes SET content = ?, created_at = ? WHERE id = ?;");

                    stmt.setString(1, txt_input.getText());
                    stmt.setString(2, Calendar.getInstance().getTime().toString());
                    stmt.setInt(3, this.id);
                    stmt.execute();
                    this.editMode = false;
                } else {
                    if(this.notebook.equals(""))
                        throw new NoNotebookSelectedException();
                    PreparedStatement stmt = notesDB.prepareStatement("INSERT INTO notes (content, owner, notebook, created_at) VALUES(?, ?, ?, ?)");
                    stmt.setString(1, txt_input.getText());
                    stmt.setString(2, this.name);
                    stmt.setString(3, this.notebook);
                    stmt.setString(4, Calendar.getInstance().getTime().toString());
                    stmt.execute();
                }
            } catch (SQLException|NoNotebookSelectedException err) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText(err.getMessage());
                error.show();
            }
        }
        this.txt_input.setText("");
        this.lbl_createdDate.setText("");
        this.initializeNotes();
        this.stopInput();
    }

    /*
     * Logs the user off and shows the LogIn form.
     *
     * @param none
     * @return none
     * @view LogIn.fxml
     */
    @FXML
    public void cmd_logOff() {
        try {
            File file = new File("user");
            if(file.exists())
                file.delete();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/LogIn.fxml"));
            Parent root = loader.load();
            Stage view = (Stage) lv_notes.getScene().getWindow();

            view.getIcons().add(new Image("file:ic_format_align_right_black_48dp.png"));
            view.setTitle("Log In");
            view.setScene(new Scene(root, 300, 275));
            view.show();
        }
        catch(IOException err) {
            System.err.println("IOException");
        }
    }

    /*
     * Activates the TextArea fot the user input, shows the save button
     * and hides the new button.
     *
     * @param none
     * @return none
     * @view TextArea &  Save
     */
    @FXML
    public void cmd_create() {
        this.lbl_createdDate.setText("");
        this.mi_create.setVisible(false);
        this.mi_save.setVisible(true);
        this.txt_input.setVisible(true);
        this.txt_input.requestFocus();
    }

    @FXML
    public void cmd_activateNewNotebookField() {
        txt_newBook.setVisible(true);
        txt_newBook.requestFocus();
    }

    @FXML
    public void cmd_createNewNotebook() {
        String bookTitle = txt_newBook.getText().replaceAll(" ", "");
        if(!bookTitle.isEmpty()){
            try {
                PreparedStatement stmt = notesDB.prepareStatement("INSERT INTO notebooks(owner, name) VALUES(?,?);");
                stmt.setString(1, name);
                stmt.setString(2, bookTitle);
                stmt.execute();
                txt_newBook.setText("");
                this.initializeNotebooks();
            }catch(SQLException err) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText(err.getMessage());
                error.show();
            }
        }
        txt_newBook.setVisible(false);
    }

    @FXML
    public void cmd_showAll() {
        this.notebook = "";
        this.initializeNotes();
        this.initializeNotebooks();
    }
}
