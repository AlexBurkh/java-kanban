import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtasksHandlerTest extends BaseHttpHandlerTest {


    @BeforeEach
    public void start() throws Exception {
        hts.start();
        String epicBody = "{\"name\":\"epic\",\"description\":\"test epic\",\"status\":\"NEW\",\"subtasksId\":[ ]}";
        var r1 = sendPOST(epicsUrl, epicBody);
    }

    @Test
    public void addTask() {
        String subtaskBody = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        try {
            var addTaskResponse = sendPOST(subtasksUrl, subtaskBody);
            var getTasksResponse = sendGET(subtasksUrl + "/2");
            var subtask = gson.fromJson(getTasksResponse.body(), Subtask.class);
            assertEquals(201, addTaskResponse.statusCode());
            assertEquals("2025-04-18T04:05:50.000", subtask.getStartTime().format(dtf));
            assertEquals(TaskStatus.NEW, subtask.getStatus());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void updateSubtaskTest() {
        String subtaskBody = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        String changedSubtaskBody = "{\"epicId\":1,\"id\":2,\"name\":\"subtask1\",\"description\":\"test subtask\"," +
                "\"status\":\"DONE\",\"startTime\":\"2025-04-18T05:05:50.000\",\"duration\":30}";
        try {
            var addSubtaskResponse = sendPOST(subtasksUrl, subtaskBody);
            var updateResponse = sendPOST(subtasksUrl, changedSubtaskBody);
            var getTaskResponse = sendGET(subtasksUrl + "/2");
            var task = gson.fromJson(getTaskResponse.body(), Subtask.class);
            assertEquals(200, updateResponse.statusCode());
            assertEquals("2025-04-18T05:05:50.000", task.getStartTime().format(dtf));
            assertEquals(30, task.getDuration().toMinutes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void deleteSubTaskTest() {
        String subtaskBody = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        try {
            var r1 = sendPOST(subtasksUrl, subtaskBody);
            var deleteResponse = sendDELETE(subtasksUrl + "/2");
            assertEquals(200, deleteResponse.statusCode());
            var getDeletedTaskResponse = sendGET(subtasksUrl + "/1");
            assertEquals(404, getDeletedTaskResponse.statusCode());
            var epicResponse = sendGET(epicsUrl + "/1");
            Epic epic = gson.fromJson(epicResponse.body(), Epic.class);
            assertEquals(0, epic.getSubTasks().size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void subtaskDeletedIfEpicDeleted() {
        String subtaskBody = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        try {
            var r1 = sendPOST(subtasksUrl, subtaskBody);
            var r2 = sendDELETE(epicsUrl + "/1");
            var r3 = sendGET(subtasksUrl);
            List<Subtask> subtasks = gson.fromJson(r3.body(), new TaskListTypeToken().getType());
            assertEquals(0, subtasks.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterEach
    public void stop() {
        hts.stop();
    }
}