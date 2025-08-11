import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var history = tm.getHistory();
        if (!history.isEmpty()) {
            send(exchange, OK, gson.toJson(history));
        } else {
            sendNotFound(exchange, "History is empty");
        }
    }
}
