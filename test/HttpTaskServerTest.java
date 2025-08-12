import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HttpTaskServerTest {
    HttpTaskServer hts = new HttpTaskServer(Managers.getDefault());
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void start() {
        hts.start(true);
    }

    @AfterEach
    public void stop() {
        hts.stop();
    }



    @Test
    public void shouldAddNewTasks() {
        String addTask1Body = "{\"name\":\"task1\",\"description\":\"description1\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-08-15T16:44:28.122\",\"duration\":20}";
        String tasksUrl = "http://localhost:8080/tasks";
        try {
            var response = sendPOST(tasksUrl, addTask1Body);
            assertEquals("Задача с id: 1 успешно добавлена", response.body());
            assertEquals(201, response.statusCode());

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
}