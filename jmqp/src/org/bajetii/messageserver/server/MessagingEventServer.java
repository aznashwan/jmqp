package org.bajetii.messageserver.server;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bajetii.messageserver.server.queues.PersonalMessageQueue;
import org.bajetii.messageserver.server.queues.TopicMessageQueue;
import org.bajetii.messageserver.server.events.IEvent;
import org.bajetii.messageserver.server.events.IEventHandler;
import org.bajetii.messageserver.server.events.IEventDispatcher;


/**
 * MessagingEventServer is the class of the main messaging server.
 * <p>
 */
public class MessagingEventServer implements IEventDispatcher {

    /**
     * maxServerTimeout is the maximum timeout in Seconds that
     * topic messages are allowed.
     * <p>
     * It overrides any later settings which may or may
     * not come from within the header of the message itself.
     */
    private int maxServerTimeout;

    /**
     * handlers is the list of event handlers.
     */
    private List<IEventHandler> handlers;

    /**
     * A messaging server is created given...
     * TODO: maxTimeout, port
     */
    public MessagingEventServer(int maxServerTimeout) {
        this.maxServerTimeout = maxServerTimeout;
        handlers = Collections.synchronizedList(
            new ArrayList<IEventHandler>()
        );

        handlers.add(new NullEventHandler(this));
    }

    /**
     * addPersonalQueue creates a new entry in the personalQueues List
     * with the given name and an empty PersonalMessageQueue.
     * <p>
     *
     * @param   name    the name of the person the new MessagingQueue is for.
     */
    public void addPersonalQueue(String name) {
        // NOTE: always add first so as to permanently have the null handler last:
        this.handlers.add(0, new PersonalMessageQueue(name, 10)); // NOTE
    }

    /**
     * addTopicQueue creates a new entry in the topicQueues List with
     * the given name and an empty TopicMessageQueue.
     */
    public void addTopicQueue(String topic) {
        this.handlers.add(0, new TopicMessageQueue(topic, 5)); // NOTE
    }

    /**
     * dispatchEvent dispatches on a given IEvent:
     */
    public String dispatchEvent(IEvent e) {
        String res = "";

        synchronized(this.handlers) {
            for(IEventHandler handler : this.handlers) {
                if(handler.cares(e)) {
                    res = handler.handleEvent(e);
                    break;
                }
            }
        }

        return res;
    }

}
