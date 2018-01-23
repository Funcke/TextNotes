package sample;

import java.util.Date;

/*
 * @author Jonas Funcke
 * @use Represents a note object
 */
public class Note implements Comparable<Note>{
    private String content;
    private String creation;
    private Date modification;
    private int id;

    public Note(int id, String cont, String created_at) {
        this.id = id;
        this.content = cont;
        this.creation = created_at;
    }

     int getId() {
        return this.id;
    }

    String getContent() {
        return this.content;
    }

    String getCreation() {
        return creation;
    }

    String getModification() {
        return this.modification.toString();
    }

    public  int compareTo(Note other) {
        return Integer.compare(other.id, this.id);
    }
}
