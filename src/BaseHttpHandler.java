import Exceptions.EpicNotExistsException;
import Exceptions.NotFoundException;
import Exceptions.TasksOverlapsException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final int OK = 200;
    protected static final int ADDED = 201;
    protected static final int CLIENT_ERROR = 400;
    protected static final int NOT_FOUNT = 404;
    protected static final int OVERLAPS = 406;
    protected static final int INTERNAL_ERROR = 500;

    protected TaskManager tm;
    protected Gson gson;


    protected BaseHttpHandler(TaskManager tm) {
        this.tm = tm;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }


    protected void send(HttpExchange e, int returnCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        e.sendResponseHeaders(returnCode, resp.length);
        e.getResponseBody().write(resp);
        e.close();
    }

    protected void sendOK(HttpExchange e) throws IOException {
        send(e, OK, "");
    }

    protected void sendAdded(HttpExchange e) throws IOException {
        send(e, ADDED, "");
    }

    protected void sendIncorrectData(HttpExchange e) throws IOException {
        send(e, CLIENT_ERROR, "");
    }

    protected void sendNotFound(HttpExchange e) throws IOException {
        send(e, NOT_FOUNT, "");
    }

    protected void sendHasOverlaps(HttpExchange e) throws IOException {
        send(e, OVERLAPS, "");
    }

    protected void sendInternalError(HttpExchange e) throws IOException {
        send(e, INTERNAL_ERROR, "");
    }

    protected void handleGet(HttpExchange e, String[] pathParts, Class<? extends Task> entity) throws IOException {
        if (pathParts.length == 2) {
            if (entity.equals(Task.class)) {
                send(e, OK, gson.toJson(tm.getTasks()));
            } else if (entity.equals(Subtask.class)) {
                send(e, OK, gson.toJson(tm.getSubTasks()));
            } else if (entity.equals(Epic.class)) {
                send(e, OK, gson.toJson(tm.getEpics()));
            }
        } else if (pathParts.length == 3) {
            String idStr = pathParts[2];
            try {
                int id = Integer.parseInt(idStr);
                if (entity.equals(Task.class)) {
                    var task = tm.getTaskById(id);
                    send(e, OK, gson.toJson(task));
                } else if (entity.equals(Subtask.class)) {
                    var subtask = tm.getSubTaskById(id);
                    send(e, OK, gson.toJson(subtask));
                } else if (entity.equals(Epic.class)) {
                    var epic = tm.getEpicById(id);
                    send(e, OK, gson.toJson(epic));
                }
            } catch (NotFoundException ex) {
                sendNotFound(e);
            } catch (NumberFormatException ex) {
                sendIncorrectData(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }

    protected void handlePost(HttpExchange e, String[] pathParts, Class<? extends Task> entity) throws IOException {
        if (pathParts.length == 2) {
            try (InputStream is = e.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task object = gson.fromJson(body, entity);
                try {
                    if (body.contains("id")) {
                        if (entity.equals(Task.class)) {
                            tm.updateTask(object);
                        } else if (entity.equals(Subtask.class)) {
                            tm.updateSubTask((Subtask) object);
                        } else if (entity.equals(Epic.class)) {
                            tm.updateEpic((Epic) object);
                        }
                        sendOK(e);
                    } else {
                        if (entity.equals(Task.class)) {
                            tm.addTask(object);
                        } else if (entity.equals(Subtask.class)) {
                            tm.addSubTask((Subtask) object);
                        } else if (entity.equals(Epic.class)) {
                            tm.addEpic((Epic) object);
                        }
                        sendAdded(e);
                    }
                } catch (JsonSyntaxException ex) {
                    sendIncorrectData(e);

                } catch (EpicNotExistsException ex) {
                    sendNotFound(e);

                }
            } catch (TasksOverlapsException ex) {
                sendHasOverlaps(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }

    protected void handleDelete(HttpExchange e, String[] pathParts, Class<? extends Task> entity) throws IOException {
        if (pathParts.length == 3) {
            String idStr = pathParts[2];
            try {
                var id = Integer.parseInt(idStr);
                if (entity.equals(Task.class)) {
                    tm.removeTaskById(id);
                } else if (entity.equals(Subtask.class)) {
                    tm.removeSubtaskById(id);
                } else if (entity.equals(Epic.class)) {
                    tm.removeEpicById(id);
                }
                send(e, OK, "");
            } catch (NotFoundException ex) {
                sendNotFound(e);
            } catch (NumberFormatException ex) {
                sendIncorrectData(e);
            }
        } else {
            sendNotFound(e);
        }
    }
}




