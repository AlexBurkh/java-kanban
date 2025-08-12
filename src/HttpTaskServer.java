import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;

public class HttpTaskServer {
    private TaskManager tm;
    private HttpServer server;

    public HttpTaskServer(TaskManager tm) {
        this.tm = tm;
    }

    public static void main(String[] args) {
        HttpTaskServer hts = new HttpTaskServer(Managers.getDefault());
        hts.start(true);
    }

    public void start(boolean debug) {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TasksHandler(tm, true));
            server.createContext("/subtasks", new SubtasksHandler(tm, true));
            server.createContext("/epics", new EpicsHandler(tm, true));
            server.createContext("/history", new HistoryHandler(tm, true));
            server.createContext("/prioritized", new PrioritizedHandler(tm, true));
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
