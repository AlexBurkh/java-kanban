import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;

public class HttpTaskServer {

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        String a = "{\n" +
                "\t\"name\" : \"task1\",\n" +
                "\t\"description\" : \"task1 description\",\n" +
                "\t\"status\" : \"NEW\",\n" +
                "\t\"startTime\" : \"2025-08-11T14:30:28.122\",\n" +
                "\t\"duration\" : 14\n" +
                "}";
        var handler = new TasksHandler(tm);
        handler.test(a);
        try {

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TasksHandler(tm));
            //server.createContext("/subtasks", new SubtasksHandler(tm));
            //server.createContext("/epics", new EpicsHandler(tm));
            //server.createContext("/history", new HistoryHandler(tm));
            //server.createContext("/prioritized", new PrioritizedHandler(tm));
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить сервер");
            e.printStackTrace();
        }
    }
}
