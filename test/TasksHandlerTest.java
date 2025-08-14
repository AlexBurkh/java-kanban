import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TasksHandlerTest extends BaseHttpHandlerTest {


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
        assertEquals(200, getTaskResponse.statusCode());
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

    @AfterEach
    public void stop() {
        hts.stop();
    }
}