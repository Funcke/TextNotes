package sample;

import java.util.Date;

/*
 * @author Jonas Funcke
 * @use Represents a note object
 */
public class Note {
    private String content;
    private Date creation;
    private Date modification;
    private int id;

    public Note(int id, String cont) {
        this.id = id;
        this.content = cont;
    }

    public int getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public String getCreation() {
        return creation.toString();
    }

    public String getModification() {
        return this.modification.toString();
    }
}
