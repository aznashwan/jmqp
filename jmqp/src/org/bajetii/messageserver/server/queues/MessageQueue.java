package org.bajetii.messageserver.server.queues;


import java.util.ArrayList;

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
     * messages is the ArrayList of message objects.
     */
    protected ArrayList<IMessage> messages;


    /**
     * A MessageQueue's construction involves the instantiation of its
     * internal ArrayList which holds its messages.
     */
    protected MessageQueue() {
        this.messages = new ArrayList<IMessage>();
    }

    /**
     * addMessage adds the given message to the internal message list.
     * <p>
     * It is meant to be used by inheriting classes alongside the respective
     * addition logic of each queue in a synchronized manner.
     */
    @Override
    public void addMessage(IMessage message) {
        this.messages.add(message);
    }

    /**
     * getMessage returns the first message available in the queue.
     * <p>
     * It is meant to be used by inheriting classes alongside the respective
     * getting logic of each queue type in a synchronized manner.
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
     * In order to avoid any escaped internal state problems; getMessages
     * returns a new Array with the contained IMessages.
     * Considering that the IMessage interface exposes no mutable operations,
     * it is safe to not bother deep-copying the IMessages themselves.
     */
    @Override
    public IMessage[] getMessages() {
        return this.messages.toArray(new IMessage[this.messages.size()]);
    }

    /**
     * cleanup performs cleanup operations on the queue's messages.
     * <p>
     * It is meant to be implemented in all inheriting classes;
     * and be made to be thread-safe.
     */
    public abstract void cleanup();

}
