import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {
    private TaskManager tm;

    public TasksHandler(TaskManager tm) {
        this.tm = tm;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET")) {
            handleGet(exchange, pathParts);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePost(exchange, pathParts);
        }
    }

    private void handleGet(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            sendText(e, gson.toJson(tm.getTasks()));
        } else if (pathParts.length == 3) {
            String taskIdInStr = pathParts[2];
            try {
                var taskId = Integer.parseInt(taskIdInStr);
                var task = tm.getTaskById(taskId);
                sendText(e, gson.toJson(task));
            } catch (NumberFormatException | NullPointerException ex) {
                sendNotFound(e);
            }
        } else {
            sendNotFound(e);
        }
    }

    // /task?name=name&description=description&status=NEW&startTime=2025-08-11T13:26:14.333&duration=14
    private void handlePost(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            try (InputStream is = e.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println(body);
                Task task = gson.fromJson(body, Task.class);
                try {
                    // Task task = gson.fromJson(body, Task.class);
                    if (body.contains("id=")) {
                        tm.updateTask(task);
                    } else {
                        tm.addTask(task);
                    }
                } catch (JsonSyntaxException ex) {
                    sendNotFound(e);
                }
            }
        } else {
            sendNotFound(e);
        }
    }
}
