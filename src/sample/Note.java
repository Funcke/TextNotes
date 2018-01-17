package sample;

import java.util.Date;

/*
 * @author Jonas Funcke
 * @use Represents a note object
 */
public class Note {
    private String content;
    private String creation;
    private Date modification;
    private int id;

    public Note(int id, String cont, String created_at) {
        this.id = id;
        this.content = cont;
        this.creation = created_at;
    }

    public int getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public String getCreation() {
        return creation;
    }

    public String getModification() {
        return this.modification.toString();
    }
}
