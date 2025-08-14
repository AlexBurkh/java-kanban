import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicsHandlerTest extends BaseHttpHandlerTest {
    @BeforeEach
    public void start() {
        hts.start();
        try {
            String epicBody = "{\"name\":\"epic\",\"description\":\"test epic\",\"status\":\"NEW\",\"subtasksId\":[ ]}";
            var r1 = sendPOST(epicsUrl, epicBody);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void epicStartTimeChanged() {
        String subtaskBody = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        try {
            var r1 = sendPOST(subtasksUrl, subtaskBody);
            var r2 = sendGET(epicsUrl + "/1");
            var epic = gson.fromJson(r2.body(), Epic.class);
            assertEquals(201, r1.statusCode());
            assertEquals("2025-04-18T04:05:50.000", epic.getStartTime().format(dtf));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void epicEndTimeChanged() {
        String subtaskBody = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T05:05:50.000\",\"duration\":40}";
        try {
            var r1 = sendPOST(subtasksUrl, subtaskBody);
            var r2 = sendGET(epicsUrl + "/1");
            var epic = gson.fromJson(r2.body(), Epic.class);
            assertEquals(201, r1.statusCode());
            assertEquals("2025-04-18T05:45:50.000", epic.getEndTime().format(dtf));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void epicsSubtasksNumberChanged() {
        String subtask1Body = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        String subtask2Body = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T05:05:50.000\",\"duration\":40}";
        try {
            var r1 = sendPOST(subtasksUrl, subtask1Body);
            var r2 = sendPOST(subtasksUrl, subtask2Body);
            var r3 = sendGET(epicsUrl + "/1");
            assertEquals(201, r1.statusCode());
            assertEquals(201, r2.statusCode());
            Epic epic = gson.fromJson(r3.body(), Epic.class);
            assertEquals(2, epic.getSubTasks().size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void epicStatusChanged() {
        String subtaskBody = "{\"epicId\":1,\"name\":\"subtask1\",\"description\":\"test subtask\",\"status\":\"NEW\"," +
                "\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        String changedSubtaskBody = "{\"epicId\":1,\"id\":2,\"name\":\"subtask1\",\"description\":\"test subtask\"," +
                "\"status\":\"DONE\",\"startTime\":\"2025-04-18T04:05:50.000\",\"duration\":40}";
        try {
            var r1 = sendPOST(subtasksUrl, subtaskBody);
            assertEquals(201, r1.statusCode());
            var r2 = sendPOST(subtasksUrl, changedSubtaskBody);
            assertEquals(200, r2.statusCode());
            var r3 = sendGET(epicsUrl + "/1");
            Epic epics = gson.fromJson(r3.body(), Epic.class);
            assertEquals(TaskStatus.DONE, epics.getStatus());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @AfterEach
    public void stop() {
        hts.stop();
    }
}