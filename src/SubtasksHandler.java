import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {
    protected SubtasksHandler(TaskManager tm) {
        super(tm);
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

    private void handleDelete(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 3) {
            String subtaskIdStr = pathParts[2];
            try {
                var subtaskId = Integer.parseInt(subtaskIdStr);
                tm.removeSubtaskById(subtaskId);
                sendText(e, 200, "");
            } catch (NumberFormatException | NotFoundException ex) {
                sendNotFound(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }
}
