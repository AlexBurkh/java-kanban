import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager tm, boolean debug) {
        super(tm, debug);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var history = tm.getHistory();
        if (!history.isEmpty()) {
            send(exchange, OK, gson.toJson(history));
        } else {
            if (debug) {
                sendNotFound(exchange, "History is empty");
            } else {
                sendNotFound(exchange, "");
            }

        }
    }
}
