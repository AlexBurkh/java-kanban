import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var prioritized = tm.getPrioritizedTasks();
        if (!prioritized.isEmpty()) {
            send(exchange, OK, gson.toJson(tm.getPrioritizedTasks()));
        } else {
            sendNotFound(exchange, "Задач нет, невозможно вывести в приоритете");
        }
    }
}
