import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {
    protected EpicsHandler(TaskManager tm) {
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
            sendText(e, 200, gson.toJson(tm.getEpics()));
        } else if (pathParts.length == 3) {
            String epicIdStr = pathParts[2];
            try {
                var epicId = Integer.parseInt(epicIdStr);
                var epic = tm.getEpicById(epicId);
                sendText(e, 200, gson.toJson(epic));
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
                var epic = gson.fromJson(body, Epic.class);
                try {
                    if (body.contains("id")) {
                        tm.updateEpic(epic);
                        sendText(e, 200, "Задача с id: " + epic.getId() + " обновлена");
                    } else {
                        tm.addEpic(epic);
                        sendText(e, 201, "Задача с id: " + epic.getId() + " успешно добавлена");
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
            String epicIdStr = pathParts[2];
            try {
                var epicId = Integer.parseInt(epicIdStr);
                tm.removeEpicById(epicId);
                sendText(e, 200, "");
            } catch (NumberFormatException | NotFoundException ex) {
                sendNotFound(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }
}
