package org.bajetii.messageserver.server.handlers;


import java.util.Map;
import java.io.IOException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import org.bajetii.messageserver.server.events.IEventDispatcher;
import org.bajetii.messageserver.server.events.ReadEvent;
import org.bajetii.messageserver.server.queues.exceptions.MessageQueueEmptyException;
import org.bajetii.messageserver.server.exceptions.MessageServerPersonNotFoundException;
import org.bajetii.messageserver.server.exceptions.MessageServerTopicNotFoundException;


/**
 * QueryHandler is the httpserver.HttpHandler implementation which deals with
 * serving GET request related to message reading.
 * <p>
 * ### !!!  Query Message Composition:
 *          A query request message's headers must contain the following:
 *              - 'Type' :: the type of the message; either 'Topic' or 'Personal'
 *              - 'To'   :: the name of the person/topic queried
 *
 *          To 'query' the server for the given messages; the aforementioned
 *          structure must be GETed from the server.
 *
 *          Query Response Composition:
 *          A query response's headers will contain either:
 *              - 400 BadRequest for bad header composition.
 *              - 404 NotFound if topic/username is inexistent
 *              - 200 OK if ok
 *          A query's response body will consist of either:
 *              - the requested message
 *              - a discriptive error message
 * ### !!!
 */
public class QueryHandler extends Handler {

    /**
     * A QueryHandler is created provided the IEventDispatcher it represents.
     */
    public QueryHandler(IEventDispatcher ms) {
         super(ms);
    }

    /**
     * handle is the main method which handles an incoming request.
     * <p>
     * It recieves the HttpExchange object; reads the request details, and
     * constructs and retuns the appropriate response.
     *
     * @param   ex      HttpExchange object to be operated on
     */
    @Override
    public void handle(HttpExchange ex) throws IOException {
        Headers headers = ex.getRequestHeaders();

        System.out.println("QueryHandler handle() method has been called.");

        // first; check the headers for 'Type':
        RequestType type = RequestType.PERSONAL;
        if(headers.containsKey("Type")) {
            // then; check that it is either 'Topic' or 'Personal':
            String typ = headers.get("Type").get(0);
            System.out.println("TYP IS ::: " + typ);
            if(!this.checkType(typ)) {
                this.errorBadHeader(ex, badTypeErrorFormat);
                return;
            }

            if(typ.equals("Topic")) {

                type = RequestType.TOPIC;
            } else if(typ.equals("Personal")) {

                type = RequestType.PERSONAL;
            } else {

                this.errorBadHeader(ex, MessageHandler.badTypeErrorFormat);
                return;
            }
        } else {
            System.out.println("I have no type!!!");
            this.errorBadHeader(ex, "No 'Type' header field provided.");
            return;
        }

        System.out.println("TYPE.toString() IS ::: " + type.toString());

        // then; check for the 'To' field:
        String target = "";
        if(headers.containsKey("To")) {
            target = headers.get("To").get(0);
            System.out.println("Message to be sent to: " + target);
        } else {
            System.out.println("'To' not provided: " + headers.containsKey("To"));
            this.errorBadHeader(ex, "No 'To' header field provided.");
            return;
        }


        String result = "";
        // check if the request is for a topic discussion or not:
        if(type.equals(RequestType.TOPIC)) {

            try {
                Map<String, String> hds = this.getHeaderSpecs(ex.getRequestHeaders());
                hds.put("EventType", "GET");

                result = this.eventDispatcher.dispatchEvent(new ReadEvent(hds, "Topic"));
            } catch(MessageServerTopicNotFoundException e) {
                this.errorMissingResource(ex, "Requested Topic is missing: " + target);
                return;
            } catch(MessageQueueEmptyException e) {
                this.errorMissingResource(ex, "Queue for topic " + target + " is empty.");
                return;
            }
        } else { // NOTE: guaranteed to be RequestType.PERSONAL otherwise.
            System.out.println("Fetching personal message for " + target);
            try {
                Map<String, String> hds = this.getHeaderSpecs(ex.getRequestHeaders());
                hds.put("EventType", "GET");

                result = this.eventDispatcher.dispatchEvent(new ReadEvent(hds, "Personal"));
            } catch(MessageServerPersonNotFoundException e) {

                this.errorMissingResource(ex, "Requested username is missing: " + target);
                return;
            } catch(MessageQueueEmptyException e) {

                this.errorMissingResource(ex, "Queue for user " + target + " is empty.");
                return;
            }
        }


        // if here; it means the message was succesfully fetched:
        ex.sendResponseHeaders(200, result.length() + 1);
        this.writeToOutputStream(ex.getResponseBody(), result);
    }
}
