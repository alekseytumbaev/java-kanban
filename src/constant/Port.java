package constant;

public enum Port {
    TASK_SERVER(8080),
    KV_SERVER(8078);

    public final int port;
    Port(int port) {
        this.port = port;
    }
}
