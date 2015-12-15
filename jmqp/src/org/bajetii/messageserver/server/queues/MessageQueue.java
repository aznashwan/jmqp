package org.bajetii.messageserver.server.queues;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bajetii.messageserver.server.events.IEvent;
import org.bajetii.messageserver.server.messages.IMessage;
import org.bajetii.messageserver.server.queues.exceptions.MessageQueueEmptyException;


/**
 * MessageQueue is the base abstract class for all MessageQueues.
 * <p>
 * what the queue is for.
 * It holds all the messages within a simple ArrayList on which it performs all
 * of the add/get operations.
 */
public abstract class MessageQueue implements IMessageQueue {

    /**
     * to is the name/id of the entity this queue is decicated to:
     */
    protected String to;

    /**
     * messages is the synchronized List of message objects.
     */
    protected List<IMessage> messages;


    /**
     * A MessageQueue's construction involves the instantiation of its
     * internal ArrayList which holds its messages.
     * <p>
     * @param   to  String name of the object of the queue.
     */
    protected MessageQueue(String to) {
        this.to = to;
        this.messages = Collections.synchronizedList(new ArrayList<IMessage>());
    }

    /**
     * addMessage adds the given message to the internal message list.
     */
    @Override
    public void addMessage(IMessage message) {
        this.messages.add(message);
    }

    /**
     * getMessage returns the first message available in the queue.
     *
     * @throws  MessageQueueEmptyException if the queue is empty.
     */
    public IMessage getMessage() {
        if(this.messages.size() == 0) {
            throw new MessageQueueEmptyException();
        }

        return this.messages.get(0);
    }

    /**
     * getMessages returns all the messages currently in the queue.
     * <p>
     */
    @Override
    public IMessage[] getMessages() {
        return this.messages.toArray(new IMessage[this.messages.size()]);
    }

    /**
     * cleanup performs cleanup operations on the queue's messages.
     * <p>
     */
    public abstract void cleanup();

    /**
     * cares returns true if the provided IMessage's meta 'To' is set to the
     * given topic 'to'.
     * <p>
     * @param   IEvent  the IEvent to check interest for.
     * @return  boolean true if the provided IEvent is interesting and will be
     *                  handled by this Queue.
     */
    public boolean cares(IEvent e) {
        // NOTE: 'To' guaranteed to be in the meta of the event:
        return e.getEventMetadata().get("To").equals(this.to);
    }

    /**
     * handleEvent handles the given IEvent and returns a String response:
     */
    public String handleEvent(IEvent e) {
        // NOTE: EventType guaranteed to be present:
        String action = e.getEventMetadata().get("EventType");

        if(action.equals("GET")) {
            try {
                return this.getMessage().getStringValue();
            } catch(MessageQueueEmptyException ex) {
                return "No message in queue for '" + this.to + "'.";
            }
        } else {
            // guaranteed to pe a "PUT/POST":
            this.addMessage(e);
            return "Message succesfully added for '" + this.to + "'.";
        }
    }

}
