import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {
    private TaskManager tm;

    public EpicsHandler(TaskManager tm) {
        this.tm = tm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
