package org.bajetii.messageserver.server.queues;


import org.bajetii.messageserver.server.events.IEvent;
import org.bajetii.messageserver.server.events.IEventHandler;
import org.bajetii.messageserver.server.messages.IMessage;


/**
 * IMessageQueue represents all the operations expected of any of the
 * messaging queues to be used internally within the server.
 * <p>
 * A messaging queue is expected to provide a thread-safe way of
 * storing and rerieving messages.
 */
public interface IMessageQueue extends IEventHandler {

    /**
     * addMessage adds the given IMessage to the queue.
     */
    public void addMessage(IMessage message);

    /**
     * addMessage creates an IMessage from the provided IEvent and adds it.
     */
    public void addMessage(IEvent e);

    /**
     * getMessage returns the first message available in the queue.
     */
    public IMessage getMessage();

    /**
     * getMessages returns all the messages available in the queue.
     *
     * @return  IMessage[]  an Array contaning all the IMessages currently in
     *                      the Queue. Care should be taken to ensure that
     *                      copies of all volatile objects are returned so as
     *                      to avoid external state corruption.
     */
    public IMessage[] getMessages();

    /**
     * cleanup performs cleanup operations on the queue's contents.
     */
    public void cleanup();

}
