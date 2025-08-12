import Exceptions.EpicNotExistsException;
import Exceptions.NotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    protected void sendIncorrectData(HttpExchange e, String text) throws IOException {
        send(e, CLIENT_ERROR, text);
    }

    protected void sendIncorrectURL(HttpExchange e) throws IOException {
        sendIncorrectData(e,"Некорректный URL");
    }

    protected void sendIncorrectJSON(HttpExchange e) throws IOException {
        sendIncorrectData(e, "Некорректный JSON");
    }

    protected void sendNotFound(HttpExchange e, String text) throws IOException {
        send(e, NOT_FOUNT, text);
    }

    protected void sendHasOverlaps(HttpExchange e, String text) throws IOException {
        send(e, OVERLAPS, text);
    }

    protected void sendInternalError(HttpExchange e, String text) throws IOException {
        send(e, INTERNAL_ERROR, text);
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
                sendNotFound(e, ex.getMessage());
            } catch (NumberFormatException ex) {
                sendIncorrectData(e, ex.getMessage());
            }
        } else {
            sendIncorrectURL(e);
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
                        send(e, OK, "Задача с id: " + object.getId() + " обновлена");
                    } else {
                        if (entity.equals(Task.class)) {
                            tm.addTask(object);
                        } else if (entity.equals(Subtask.class)) {
                            tm.addSubTask((Subtask) object);
                        } else if (entity.equals(Epic.class)) {
                            tm.addEpic((Epic) object);
                        }
                        send(e, ADDED, "Задача с id: " + object.getId() + " успешно добавлена");
                    }
                } catch (JsonSyntaxException ex) {
                    sendIncorrectJSON(e);
                } catch (EpicNotExistsException ex) {
                    sendNotFound(e, ex.getMessage());
                }
            }
        } else {
            sendIncorrectURL(e);
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
                sendNotFound(e, ex.getMessage());
            } catch (NumberFormatException ex) {
                sendNotFound(e, "Задача с id: " + idStr + " не найдена");
            }
        } else {
            sendIncorrectURL(e);
        }
    }
}


class TasklistTypeToken extends TypeToken<List<Task>> {

}

class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime ldt) throws IOException {
        if (ldt == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(ldt.format(dtf));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String dateString = jsonReader.nextString();
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateString, dtf);
    }
}

class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(duration.toMinutes());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String durationString = jsonReader.nextString();
        if (durationString ==null || durationString.isEmpty()) {
            return null;
        }
        return Duration.ofMinutes(Long.parseLong(durationString));
    }
}
