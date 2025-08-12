import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TasksHandlerTest {
    String tasksUrl = "http://localhost:8080/tasks";
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    HttpTaskServer hts = new HttpTaskServer(Managers.getDefault());
    HttpClient client = HttpClient.newHttpClient();


    @BeforeEach
    public void start() {
        hts.start(true);
        String addTask0Body = "{\"name\":\"task0\",\"description\":\"description0\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T13:44:28.122\",\"duration\":20}";
        String addTask1Body = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T14:44:28.122\",\"duration\":20}";
        try {
            sendPOST("http://localhost:8080/tasks", addTask0Body);
            sendPOST("http://localhost:8080/tasks", addTask1Body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void addTask() {
        String addTask2Body = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T16:44:28.122\",\"duration\":20}";
        try {
            var response1 = sendPOST(tasksUrl, addTask2Body);
            assertEquals("Задача с id: 3 успешно добавлена", response1.body());
            assertEquals(201, response1.statusCode());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void overlapsTest() {
        String addTask1Body = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T14:54:28.122\",\"duration\":20}";
        String addTask2Body = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T13:30:28.122\",\"duration\":20}";
        try {
            var response1 = sendPOST(tasksUrl, addTask1Body);
            assertEquals(406, response1.statusCode());
            assertEquals("Новая задача имеет пересечения во времени с существующими задачами", response1.body());
            var response2 = sendPOST(tasksUrl, addTask2Body);
            assertEquals(406, response2.statusCode());
            assertEquals("Новая задача имеет пересечения во времени с существующими задачами", response2.body());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public HttpResponse<String> sendPOST(String url, String body)  throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendGET(String url)  throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void stop() {
        hts.stop();
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