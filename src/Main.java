import api.HttpTaskServer;
import api.kv.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HttpTaskServer();
    }
}
