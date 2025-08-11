import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {
    protected TasksHandler(TaskManager tm) {
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
            sendText(e, 200, gson.toJson(tm.getTasks()));
        } else if (pathParts.length == 3) {
            String taskIdStr = pathParts[2];
            try {
                var taskId = Integer.parseInt(taskIdStr);
                var task = tm.getTaskById(taskId);
                sendText(e, 200, gson.toJson(task));
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
                var task = gson.fromJson(body, Task.class);
                try {
                    if (body.contains("id")) {
                        tm.updateTask(task);
                        sendText(e, 200, "Задача с id: " + task.getId() + " обновлена");
                    } else {
                        tm.addTask(task);
                        sendText(e, 201, "Задача с id: " + task.getId() + " успешно добавлена");
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
            String taskIdStr = pathParts[2];
            try {
                var taskId = Integer.parseInt(taskIdStr);
                tm.removeTaskById(taskId);
                sendText(e, 200, "");
            } catch (NumberFormatException | NotFoundException ex) {
                sendNotFound(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }
}
