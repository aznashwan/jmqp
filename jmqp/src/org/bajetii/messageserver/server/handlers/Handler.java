package org.bajetii.messageserver.server.handlers;


import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

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
        if(!(type.equalsIgnoreCase("Topic") || type.equalsIgnoreCase("Personal"))) {
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
        System.out.println("error: " + message);
        ex.sendResponseHeaders(code, message.length() + 1);
        this.writeToOutputStream(ex.getResponseBody(), message);
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
        System.out.println("errorBadHeader: " + response);

        ex.sendResponseHeaders(400, response.length() + 1);
        this.writeToOutputStream(ex.getResponseBody(), response);
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
        System.out.println("errorMissingResource: " + response);

        ex.sendResponseHeaders(404, response.length() + 1);
        this.writeToOutputStream(ex.getResponseBody(), response);
    }

    /**
     * readInputStream is a helper method which reads the whole contents of the
     * given InputStream and returns its String.
     * <p>
     * @param   is  InputStream to be read.
     * @return  s   resulting String.
     */
    protected String readInputStream(InputStream is) throws IOException {
        int i;
        String buff = "";

        while((i = is.read()) != -1) {
            buff = buff + (char) i;
        }

        is.close();

        return  buff;
    }

    /**
     * writeToOutputStream is a helper method which writes the given String
     * to the given OutputStream.
     * <p>
     * @param   os  OutputStream to be written to.
     * @param   s   String to be written out.
     */
    protected void writeToOutputStream(OutputStream os, String s) throws IOException {
        System.out.println("Writing to OutputStream: " + s);

        final PrintStream printStream = new PrintStream(os);
        printStream.print(s);
        printStream.close();
        os.close();
    }

}
