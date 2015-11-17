package org.bajetii.messageserver.server.queues;


import java.util.ArrayList;

import org.bajetii.messageserver.server.messages.IMessage;
import org.bajetii.messageserver.server.queues.exceptions.MessageQueueFullException;


/**
 * PersonalMessageQueue is the MessageQueue used for handling personal
 * messages (messages with a single recipient) between clients.
 * <p>
 * It has the added functionality of popping any requested messages out of the
 * queue before returning them. And being able to hold a limited number of
 * messages at any given time.
 */
public class PersonalMessageQueue extends MessageQueue {

    /**
     * maxCapacity represents the maximum capacity of the PersonalMessageQueue.
     */
    private int maxCapacity;

    /**
     * A PersonalMessageQueue is created provided the maximum message
     * capacity it posses.
     * <p>
     * @param   maxCap  the maximum number of elements the queue can hold.
     */
    public PersonalMessageQueue(int maxCapacity) {
        super();
        this.maxCapacity = maxCapacity;
    }

    /**
     * addMessage adds the given IMessage to the queue.
     * <p>
     * Execution falls under the monitor lock to ensure thread-safety.
     * @throws  MessageQueueFullException   if the queue is at maximum capacity
     */
    @Override
    public synchronized void addMessage(IMessage message) {
        if(this.messages.size() == this.maxCapacity) {
            throw new MessageQueueFullException();
        }

        super.addMessage(message);
    }

    /**
     * getMessage returns the first element in the queue and removes it.
     * <p>
     * Execution falls under the monitor lock to ensure thread-safety.
     * @throws  MessageQueueEmptyException  if the MessageQueue has no messages
     *
     * @return  IMessage    the first IMessage in the MessageQueue.
     */
    @Override
    public synchronized IMessage getMessage() {
        IMessage message = super.getMessage();

        this.messages.remove(0);

        return message;
    }

    /**
     * getMessages returns the all the messages currently in the queue.
     * <p>
     * Execution falls under the monitor lock to ensure thread-safety.
     * Considering message getting is destructive in the context of
     * PersonalMessagingQueues; it resets the message queue after the fetch.
     *
     * @return  IMessage[]  the Array of all messages contained in the queue.
     */
    public synchronized IMessage[] getMessages() {
        IMessage[] messgs = this.messages.toArray(new IMessage[this.messages.size()]);

        this.messages = new ArrayList<IMessage>();

        return messgs;
    }

    /**
     * cleanup is no-op on PersonalMessageQueues as all the cleanup is
     * automatically handled on every getMessage(s).
     */
    @Override
    public void cleanup() {}

}
