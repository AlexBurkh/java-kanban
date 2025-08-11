import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {
    private TaskManager tm;

    public SubtasksHandler(TaskManager tm) {
        this.tm = tm;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
