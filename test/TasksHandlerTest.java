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
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();
    HttpTaskServer hts = new HttpTaskServer(Managers.getDefault());
    HttpClient client = HttpClient.newHttpClient();


    @BeforeEach
    public void start() {
        hts.start();
        String addTask1Body = "{\"name\":\"task0\",\"description\":\"description0\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T13:44:28.122\",\"duration\":20}";
        String addTask2Body = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T14:44:28.122\",\"duration\":20}";
        try {
            sendPOST(tasksUrl, addTask1Body);
            sendPOST(tasksUrl, addTask2Body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void addTask() throws Exception {
        String addTask2Body = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T16:44:28.122\",\"duration\":20}";
        var addTaskResponse = sendPOST(tasksUrl, addTask2Body);
        var getTasksResponse = sendGET(tasksUrl + "/3");
        var task = gson.fromJson(getTasksResponse.body(), Task.class);
        assertEquals(201, addTaskResponse.statusCode());
        assertEquals("2025-08-15T16:44:28.122", task.getStartTime().format(dtf));
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    public void overlapsTest() throws Exception {
        String overlaps1 = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T14:54:28.122\",\"duration\":20}";
        String overlaps2 = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T13:30:28.122\",\"duration\":20}";
        String overlaps3 = "{\"id\":1,\"name\":\"task0\",\"description\":\"description0\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T14:40:28.122\",\"duration\":20}";

        var response1 = sendPOST(tasksUrl, overlaps1);
        assertEquals(406, response1.statusCode());
        var response2 = sendPOST(tasksUrl, overlaps2);
        assertEquals(406, response2.statusCode());
        var response3 = sendPOST(tasksUrl, overlaps3);
        var overlaps3GetResponse = sendGET(tasksUrl + "/1");
        var task = gson.fromJson(overlaps3GetResponse.body(), Task.class);
        assertEquals(406, response3.statusCode());
        assertEquals("2025-08-15T13:44:28.122", task.getStartTime().format(dtf));
    }

    @Test
    public void updateTaskTest() throws Exception {
        String updateBody = "{\"id\":1,\"name\":\"task1\",\"description\":\"edited\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T16:44:28.122\",\"duration\":30}";
        var updateResponse = sendPOST(tasksUrl, updateBody);
        var getTaskResponse = sendGET(tasksUrl + "/1");
        var task = gson.fromJson(getTaskResponse.body(), Task.class);
        assertEquals(200, updateResponse.statusCode());
        assertEquals("2025-08-15T16:44:28.122", task.getStartTime().format(dtf));
        assertEquals(30, task.getDuration().toMinutes());
    }

    @Test
    public void deleteTaskTest() throws Exception {
        var deleteResponse = sendDelete(tasksUrl + "/1");
        assertEquals(200, deleteResponse.statusCode());
        var getDeletedTaskResponse = sendGET(tasksUrl + "/1");
        assertEquals(404, getDeletedTaskResponse.statusCode());
    }

    public HttpResponse<String> sendPOST(String url, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendGET(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendDelete(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
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