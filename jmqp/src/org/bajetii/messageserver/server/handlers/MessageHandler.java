package org.bajetii.messageserver.server.handlers;


import java.io.IOException;
import java.io.OutputStream;

import org.bajetii.messageserver.server.MessagingServer;
import org.bajetii.messageserver.server.queues.exceptions.MessageQueueFullException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;


/**
 * MessageHandler is the httpserver.HttpHandler implementation which deals with
 * an inbount POST/PUT message.
 * <p>
 *
 * ### !!!  Posted Message Composition:
 *          A posted message's headers must contain the following two keys:
 *              - 'Type'    :: the type of the message; either 'Topic' or 'Personal'
 *              - 'To'      :: the destination of the message be it topic or person
 *              - 'Timeout' :: if a message for a topic; the desired int timeout
 *
 *          The message body: is treated as raw text and can contain anything.
 *
 *          To 'send' a message; the aforementioned structure must be PUTed or
 *          POSTed on the specified ip/port of the server.
 *
 *          Response Composition:
 *          A reponse message's headers will contain either:
 *              - 400 BadRequest for lacking header fields
 *              - 406 Unnaceptable for writing message to person with full queue
 *              - 202 Accepted if everything went ok
 *          The body will contain an affirmatory message
 * ### !!!
 */
public class MessageHandler extends Handler {

    /**
     * acceptedResponseMessageFormat is the format of the response to be sent
     * back when a message is accepted:
     */
    protected static final String acceptedResponseMessageFormat = "The message was accepted for '%s': '%s'.";

    /**
     * A MessageHandler is created provided the MessagingServer it represents.
     */
    public MessageHandler(MessagingServer ms) {
         super(ms);
    }

    /**
     * handle is the main method which handles an incoming request.
     * <p>
     * It recieves the HttpExchange object; reads the request details, and
     * adds the required response info to said object.
     */
    @Override
    public void handle(HttpExchange ex) throws IOException {
        Headers headers = ex.getRequestHeaders();

        // first; check the headers for 'Type':
        RequestType type = RequestType.PERSONAL;
        System.out.println(headers.containsKey("Type"));
        if(headers.containsKey("Type")) {
            // then; check that it is either 'Topic' or 'Personal':
            String typ = headers.get("Type").get(0);
            System.out.println(">>>> " + typ);
            if(!this.checkType(typ)) {
                this.errorBadHeader(ex, badTypeErrorFormat);
                return;
            }

            if(typ.equalsIgnoreCase("Topic")) {
                type = RequestType.TOPIC;
            } else if(typ.equalsIgnoreCase("Personal")) {
                type = RequestType.PERSONAL;
            } else {
                this.errorBadHeader(ex, MessageHandler.badTypeErrorFormat);
                return;
            }
        } else {
            this.errorBadHeader(ex, "No 'Type' header field provided.");
            return;
        }

        // then; check for the 'To' field:
        String to = "";
        if(headers.containsKey("To")) {
            to = headers.get("To").get(0);
        } else {
            this.errorBadHeader(ex, "No 'To' header field provided.");
            return;
        }

        // now; get the body (aka the message) and do the appropriate action:
        String message = ex.getRequestBody().toString();
        System.out.println(">>>>>> MESAGE IS >>>>> " + message);

        if(type.equals(RequestType.TOPIC)) {
        	System.out.println(">>>> TOPIC, Type <<<<<");
            // check for the mandatory 'Timeout' header:
            if(!headers.containsKey("Timeout")) {
                this.errorBadHeader(ex, "No 'Timeout' header provided for topic message.");
                return;
            }

            // now; extract the 'Timeout' field:
            int timeout = 100;  // WARN: bad random default.
            try {
                timeout = Integer.parseInt(headers.get("Timeout").get(0));
            } catch(NumberFormatException e) {
                this.errorBadHeader(ex, "Could not parse provided 'Timeout' value.");
                return;
            }

            try {
                this.messagingServer.addTopicMessage(to, message, timeout);
            } catch(Exception e) {
            }
        } else {    // guaranteed to be a RequestType.PERSONAL; so we can just else:
            try {
                this.messagingServer.addPersonalMessage(to, message);
            } catch(MessageQueueFullException e) {
                this.error(ex, 406, "406 : ErrorUnnaceptable :: the person's inbox is full.");
                return;
            }
        }

        // if here; it means that we're good.
        // send out StatusAccespted and a positive response:
        String response = String.format(MessageHandler.acceptedResponseMessageFormat, to, message);
        ex.sendResponseHeaders(202, response.length());
        System.out.println(response);

        OutputStream os = ex.getResponseBody();
        os.write(("202 : StatusAccepted :: " + response).getBytes());
        os.close();
    }

}
