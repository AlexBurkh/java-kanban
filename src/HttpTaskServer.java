import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private TaskManager tm;
    private HttpServer server;

    public HttpTaskServer(TaskManager tm) {
        this.tm = tm;
    }

    public static void main(String[] args) {
        HttpTaskServer hts = new HttpTaskServer(Managers.getDefault());
        hts.start();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
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

    public void stop() {
        server.stop(0);
    }
}
