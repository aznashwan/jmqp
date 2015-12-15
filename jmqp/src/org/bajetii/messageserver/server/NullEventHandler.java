package org.bajetii.messageserver.server;


import org.bajetii.messageserver.server.events.IEvent;
import org.bajetii.messageserver.server.events.IEventHandler;


/**
 * NullEventHandler is the handler which accepts any message and returns and
 * does the appropriate action on the server:
 */
public class NullEventHandler implements IEventHandler {

    /**
     * server is the messaging server to return dispatch on.
     */
    private MessagingEventServer server;

    /**
     * A NullEventHandler is created given a MessagingEventServer to callback on.
     * <p>
     * @param   MessagingEventServer    the Server to operate on.
     */
    public NullEventHandler(MessagingEventServer server) {
        this.server = server;
    }

    /**
     * cares always returns true.
     */
    public boolean cares(IEvent e) {
        return true;
    }

    /**
     * handleEvent handles the given event by creating the required Queue.
     * <p>
     * @param   IEvent  the IEvent to be handled.
     */
    public String handleEvent(IEvent e) {
        String to = e.getEventMetadata().get("To");
        String type = e.getEventType();

        // it means the queue was not present, so we just add it:
        if(type == "Topic") {
            this.server.addTopicQueue(to);
        } else {
            this.server.addPersonalQueue(to);
        }

        // re-dispatch on the event:
        return server.dispatchEvent(e);

    }

}
