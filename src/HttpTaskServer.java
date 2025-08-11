import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;

public class HttpTaskServer {

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TasksHandler(tm));
            server.createContext("/subtasks", new SubtasksHandler(tm));
            server.createContext("/epics", new EpicsHandler(tm));
            server.createContext("/history", new HistoryHandler(tm));
            server.createContext("/prioritized", new PrioritizedHandler(tm));
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить сервер");
            e.printStackTrace();
        }
    }
}
