import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class BaseHttpHandler implements HttpHandler {
    protected Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

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

    class TasklistTypeToken extends TypeToken<List<Task>> {}

    protected void sendText(HttpExchange e, int returnCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        e.sendResponseHeaders(returnCode, resp.length);
        e.getResponseBody().write(resp);
        e.close();
    }

    protected void sendNotFound(HttpExchange e) throws IOException {
        String text = "Not Found";
        e.sendResponseHeaders(404, 0);
        e.getResponseBody().write(text.getBytes(StandardCharsets.UTF_8));
        e.close();
    }

    protected void sendHasOverlaps(HttpExchange e) throws IOException {
        String text = "Has Overlaps";
        e.sendResponseHeaders(406, 0);
        e.getResponseBody().write(text.getBytes(StandardCharsets.UTF_8));
        e.close();
    }

    protected void sendInternalError(HttpExchange e) throws IOException {
        String text = "Internal Error";
        e.sendResponseHeaders(500, 0);
        e.getResponseBody().write(text.getBytes(StandardCharsets.UTF_8));
        e.close();
    }
}
