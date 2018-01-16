package sample;

public class NoNotebookSelectedException extends Exception {
    @Override
    public String getMessage() {
        return "No Notebook seleceted";
    }
}
