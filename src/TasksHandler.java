import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    protected TasksHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET")) {
            handleGet(exchange, pathParts, Task.class);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePost(exchange, pathParts, Task.class);
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            handleDelete(exchange, pathParts, Task.class);
        }
    }
}
