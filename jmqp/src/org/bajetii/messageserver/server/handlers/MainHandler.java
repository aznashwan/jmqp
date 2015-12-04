package org.bajetii.messageserver.server.handlers;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.bajetii.messageserver.server.MessagingServer;


/**
 * MainHandler is simply a handler which encompasses both a MessageHandler and
 * a QueryHeandler.
 * <p>
 * It's only purposes is to smartly switch between the implementations of the
 * handler object depending on the request method.
 */
public class MainHandler extends Handler {

    /**
     * messageHandler and queryHandler are the two encapsulated HttpHandlers.
     */
    private HttpHandler messageHandler;
    private HttpHandler queryHandler;

    /**
     * A MainHandler; like any other Handler, is created given the
     * MessagingServer it represents.
     */
    public MainHandler(MessagingServer ms) {
        super(ms);
        this.messageHandler = new MessageHandler(ms);
        this.queryHandler = new QueryHandler(ms);
    }

    /**
     * handle simply checks the request method and passes the actual
     * handling to either the messageHandler or the queryHandler.
     * <p>
     * @param   ex      HttpExchange to be operated on
     */
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();


        if(method.equalsIgnoreCase("GET")) {
            System.out.println("Beginning fetching procedure.")

            this.queryHandler.handle(ex);
            return;
        } else if(method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
            System.out.println("Beginning posting procedure.")

            this.messageHandler.handle(ex);
            return;
        } else {
            this.error(ex, 400, "400 : BadRequest :: Mehod is not supported: " + method);
            return;
        }
    }

}

