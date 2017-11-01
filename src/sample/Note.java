package sample;

public class Note {
    private String content;
    private String owner;
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
}
