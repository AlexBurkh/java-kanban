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


    protected void sendText(HttpExchange e, int returnCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        e.sendResponseHeaders(returnCode, resp.length);
        e.getResponseBody().write(resp);
        e.close();
    }

    protected void sendIncorrectData(HttpExchange e) throws IOException {
        sendText(e, 400, "Incorrect Data");
    }

    protected void sendNotFound(HttpExchange e) throws IOException {
        sendText(e, 404, "Not Found");
    }

    protected void sendHasOverlaps(HttpExchange e) throws IOException {
        sendText(e, 406, "Has Overlaps");
    }

    protected void sendInternalError(HttpExchange e) throws IOException {
        sendText(e, 500, "Internal Error");
    }

    protected void handleGet(HttpExchange e, String[] pathParts, Class<? extends Task> entity) throws IOException {
        if (pathParts.length == 2) {
            if (entity.equals(Task.class)) {
                sendText(e, 200, gson.toJson(tm.getTasks()));
            } else if (entity.equals(Subtask.class)) {
                sendText(e, 200, gson.toJson(tm.getSubTasks()));
            } else if (entity.equals(Epic.class)) {
                sendText(e, 200, gson.toJson(tm.getEpics()));
            }
        } else if (pathParts.length == 3) {
            String idStr = pathParts[2];
            try {
                int id = Integer.parseInt(idStr);
                if (entity.equals(Task.class)) {
                    var task = tm.getTaskById(id);
                    sendText(e, 200, gson.toJson(task));
                } else if (entity.equals(Subtask.class)) {
                    var subtask = tm.getSubTaskById(id);
                    sendText(e, 200, gson.toJson(subtask));
                } else if (entity.equals(Epic.class)) {
                    var epic = tm.getEpicById(id);
                    sendText(e, 200, gson.toJson(epic));
                }
            } catch (NumberFormatException | NotFoundException ex) {
                sendNotFound(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }

    protected void handlePost(HttpExchange e, String[] pathParts, Class<? extends Task> entity) throws IOException {
        if (pathParts.length == 2) {
            try (InputStream is = e.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                var object = gson.fromJson(body, entity);
                try {
                    if (body.contains("id")) {
                        if (entity.equals(Task.class)) {
                            tm.updateTask((Task) object);
                        } else if (entity.equals(Subtask.class)) {
                            tm.updateSubTask((Subtask) object);
                        } else if (entity.equals(Epic.class)) {
                            tm.updateEpic((Epic) object);
                        }
                        sendText(e, 200, "Задача с id: " + object.getId() + " обновлена");
                    } else {
                        if (entity.equals(Task.class)) {
                            tm.addTask((Task) object);
                        } else if (entity.equals(Subtask.class)) {
                            tm.addSubTask((Subtask) object);
                        } else if (entity.equals(Epic.class)) {
                            tm.addEpic((Epic) object);
                        }
                        sendText(e, 201, "Задача с id: " + object.getId() + " успешно добавлена");
                    }

                } catch (JsonSyntaxException ex) {
                    sendNotFound(e);
                }
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
                sendText(e, 200, "");
            } catch (NumberFormatException | NotFoundException ex) {
                sendNotFound(e);
            }
        } else {
            sendIncorrectData(e);
        }
    }
}


class TasklistTypeToken extends TypeToken<List<Task>> {

}

class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime ldt) throws IOException {
        jsonWriter.value(ldt.format(dtf));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), dtf);
    }
}

class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.toMinutes());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String durationString = jsonReader.nextString();
        return Duration.ofMinutes(Long.parseLong(durationString));
    }
}
