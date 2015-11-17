package org.bajetii.messageserver.server.queues.exceptions;


/**
 * MessageQueueEmptyException is raised whenever a message is attempted to be
 * fetched from the queue whilst it is actually empty.
 * <p>
 * It extends RuntimeExceptions to avoid Java's infamous checked exception
 * mechanism, which is very bad. :(
 */
public class MessageQueueEmptyException extends RuntimeException {

    public MessageQueueEmptyException() {
        super();
    }

    public MessageQueueEmptyException(String message) {
        super("MessageQueueEmptyException: " + message);
    }

}
