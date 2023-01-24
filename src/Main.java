import task_managers.http_task_manager.kv.KVServer;
import task_managers.http_task_manager.kv.KVTaskClient;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();

        KVTaskClient client = new KVTaskClient("http://localhost:8078");

        client.put("key", "value");
        System.out.println(client.load("key"));

        client.put("key", "value changed");
        System.out.println(client.load("key"));
    }
}
