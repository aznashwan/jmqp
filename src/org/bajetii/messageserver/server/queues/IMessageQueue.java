package org.bajetii.messageserver.server.queues;


import org.bajetii.messageserver.server.messages.IMessage;

/**
 * IMessageQueue represents all the operations expected of any of the
 * messaging queues to be used internally within the server.
 * <p>
 * A messaging queue is expected to provide a thread-safe way of
 * storing and rerieving messages.
 */
public interface IMessageQueue {

    /**
     * addMessage adds the given IMessage to the queue.
     * <p>
     * Calls to addMessage actively modify the structure of the queue, and thus
     * cannot be called asynchronously.
     * It should be implemented as synchronized to ensure thread-safety.
     */
    public void addMessage(IMessage message);

    /**
     * getMessage returns the first message available in the queue.
     * <p>
     * Calls to getMessage may produce side-effects on the queue's structure.
     * It should be implemented as synchronized to ensure thread-safety.
     */
    public IMessage getMessage();

    /**
     * getMessages returns all the messages available in the queue.
     * <p>
     * Calls to getMessages may produce side-effects on the queue's structure.
     * It should be implemented as synchronized to ensure thread-safety.
     *
     * @return  IMessage[]  an Array contaning all the IMessages currently in
     *                      the Queue. Care should be taken to ensure that
     *                      copies of all volatile objects are returned so as
     *                      to avoid external state corruption.
     */
    public IMessage[] getMessages();

    /**
     * cleanup performs cleanup operations on the queue's contents.
     * <p>
     * It should be implemented as synchronized to ensure thread-safety.
     */
    public void cleanup();

}
