package constant;

public enum Endpoint {
    LOAD("/load"),
    REGISTER("/register"),
    SAVE("/save"),

    TASK("/task/"),
    SUBTASK("/subtask/"),
    EPIC("/epic/"),
    HISTORY("/history"),
    MAIN("/tasks"),
    HOST("http://localhost:");

    public final String url;
    Endpoint(String url) {
        this.url = url;
    }
}
