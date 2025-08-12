import Exceptions.EpicNotExistsException;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {
    protected EpicsHandler(TaskManager tm, boolean debug) {
        super(tm, debug);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET")) {
            handleGet(exchange, pathParts, Epic.class);
        } else if (exchange.getRequestMethod().equals("POST")) {
            handlePost(exchange, pathParts);
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            handleDelete(exchange, pathParts, Epic.class);
        }
    }

    private void handlePost(HttpExchange e, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            try (InputStream is = e.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                try {
                    if (body.contains("id")) {
                        tm.updateEpic(epic);
                        send(e, OK, "Задача с id: " + epic.getId() + " обновлена");
                    } else {
                        tm.addEpic((Epic) epic);
                        send(e, ADDED, "Задача с id: " + epic.getId() + " успешно добавлена");
                    }
                } catch (JsonSyntaxException ex) {
                    sendIncorrectJSON(e);
                } catch (EpicNotExistsException ex) {
                    sendNotFound(e, ex.getMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            sendIncorrectURL(e);
        }
    }
}
