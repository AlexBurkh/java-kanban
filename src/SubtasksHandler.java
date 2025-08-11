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
            handleGet(exchange, pathParts);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePost(exchange, pathParts);
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            handleDelete(exchange, pathParts);
        }
    }

    private void handleGet(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            sendText(e, 200, gson.toJson(tm.getSubTasks()));
        } else if (pathParts.length == 3) {
            String subtaskIdStr = pathParts[2];
            try {
                var subtaskId = Integer.parseInt(subtaskIdStr);
                var subtask = tm.getSubTaskById(subtaskId);
                sendText(e, 200, gson.toJson(subtask));
            } catch (NumberFormatException | NotFoundException ex) {
                sendNotFound(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }

    private void handlePost(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            try (InputStream is = e.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                var subtask = gson.fromJson(body, Subtask.class);
                try {
                    if (body.contains("id")) {
                        tm.updateSubTask(subtask);
                        sendText(e, 200, "Подзадача с id: " + subtask.getId() + " обновлена");
                    } else {
                        tm.addSubTask(subtask);
                        sendText(e, 201, "Подзадача с id: " + subtask.getId() + " успешно добавлена");
                    }

                } catch (JsonSyntaxException ex) {
                    sendNotFound(e);
                }
            }
        } else {
            sendIncorrectData(e);
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
