package sample;

/*
 * @author Jonas Funcke
 * @use Represents a note object
 */
public class Note {
    private String content;
    private String notebook;
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

    public String getNotebook() { return this.notebook; }
}
