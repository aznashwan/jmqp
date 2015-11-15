package org.bajetii.messageserver.server.handlers;


import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import org.bajetii.messageserver.server.MessagingServer;

/**
 * Handler is the handler which deals with deciding which action to take
 * per recieved message.
 * <p>
 * The actual handling behavior should be implemented in their respective
 * subcleasses.
 */
public abstract class Handler implements HttpHandler {

    /**
     * badTypeErrorFormat is the String format of the bad type error message.
     */
    protected static final String badTypeErrorFormat = "'Type' must be either 'Topic' or 'Personal'.";

    /**
     * messagingServer is a reference to the MessagingServer this
     * Handler represents.
     */
    protected MessagingServer messagingServer;

    /**
     * A Handler is created provided the Server is facades.
     * <p>
     * @param   server  the Server which is represented.
     */
    public Handler(MessagingServer ms) {
        this.messagingServer = ms;
    }

    /**
     * handle is the method in the HttpHandler interface which must be
     * implemented.
     * <p>
     * It recieves the HttpExchange object; reads the request details, and
     * adds the required response info to said object.
     */
    @Override
    public abstract void handle(HttpExchange ex) throws IOException;

    /**
     * checkType is a helper method used internally to check that a good type
     * string was provided.
     * <p>
     * @param   type    String representation of the given 'Type' in the
     * headers.
     */
    protected boolean checkType(String type) {
        if(!(type == "Topic" || type == "Personal")) {
            return false;
        }

        return true;
    }

    /**
     * error is a helper method which returns the given code and message fpr
     * the provided HttpExchange object.
     * <p>
     * @param   ex      HttpExchange object to be operated on
     * @param   code    int http code to be returned
     * @param   message String message to be returned
     */
    protected void error(HttpExchange ex, int code, String message) throws IOException {
        ex.sendResponseHeaders(code, message.length());

        OutputStream os = ex.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }

    /**
     * errorBadHeader is a helper method which returns a 400 and a descriptive
     * error message based on the provided message.
     * <p>
     * @param   ex      HttpExchange to be operated on.
     * @param   message auxiliary message to be appended.
     */
    protected void errorBadHeader(HttpExchange ex, String message) throws IOException {
        String response = "400 : BadRequest :: " + message;
        ex.sendResponseHeaders(400, response.length());

        OutputStream os = ex.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    /**
     * errorMissingResource is a helper method whcich returns 404 and a descriptive
     * error message based on the message provided.
     * <p>
     * @param   ex      HttpExchange to be operated on
     * @param   message String to be appended as error message
     */
    protected void errorMissingResource(HttpExchange ex, String message) throws IOException {
        String response = "404 : ErrorMissing :: " + message;
        ex.sendResponseHeaders(404, response.length());

        OutputStream os = ex.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
