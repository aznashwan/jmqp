package org.bajetii.messageserver.server.queues;


import org.bajetii.messageserver.server.events.IEvent;
import org.bajetii.messageserver.server.messages.IMessage;
import org.bajetii.messageserver.server.messages.StringMessage;
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
     * capacity it posseses.
     * <p>
     * @param   to      String name of the object of the Queue.
     * @param   maxCap  the maximum number of elements the queue can hold.
     */
    public PersonalMessageQueue(String to, int maxCapacity) {
        super(to);
        this.maxCapacity = maxCapacity;
    }

    /**
     * addMessage adds the given IMessage to the queue.
     * <p>
     *
     * @throws  MessageQueueFullException   if the queue is at maximum capacity
     */
    @Override
    public void addMessage(IMessage message) {
        if(this.messages.size() == this.maxCapacity) {
            throw new MessageQueueFullException();
        }

        super.addMessage(message);
    }

    /**
     * addMessage adds the given IEvent as an IMessage.
     */
    public void addMessage(IEvent e) {
         if(this.messages.size() == this.maxCapacity) {
            throw new MessageQueueFullException();
        }

        super.addMessage(new StringMessage(e.getEventMetadata(), e.getEventPayload()));
    }

    /**
     * getMessage returns the first element in the queue and removes it.
     * <p>
     *
     * @throws  MessageQueueEmptyException  if the MessageQueue has no messages
     *
     * @return  IMessage    the first IMessage in the MessageQueue.
     */
    @Override
    public IMessage getMessage() {
        IMessage message = super.getMessage();

        this.messages.remove(0);

        return message;
    }

    /**
     * getMessages returns the all the messages currently in the queue.
     * <p>
     * Considering message getting is destructive in the context of
     * PersonalMessagingQueues; it resets the message queue after the fetch.
     *
     * @return  IMessage[]  the Array of all messages contained in the queue.
     */
    public IMessage[] getMessages() {
        IMessage[] messgs = this.messages.toArray(new IMessage[this.messages.size()]);

        this.messages.clear();

        return messgs;
    }

    /**
     * cleanup is no-op on PersonalMessageQueues as all the cleanup is
     * automatically handled on every getMessage(s).
     */
    @Override
    public void cleanup() {}

}
