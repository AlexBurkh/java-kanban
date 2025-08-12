import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {
    protected SubtasksHandler(TaskManager tm, boolean debug) {
        super(tm, debug);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET")) {
            handleGet(exchange, pathParts, Subtask.class);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePost(exchange, pathParts, Subtask.class);
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            handleDelete(exchange, pathParts, Subtask.class);
        }
    }
}
