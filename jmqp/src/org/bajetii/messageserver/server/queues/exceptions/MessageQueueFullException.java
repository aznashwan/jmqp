package org.bajetii.messageserver.server.queues.exceptions;


/**
 * MessageQueueFullException is thown whenever a message queue is requested to
 * add a new message but the queue is already full.
 * <p>
 * It extends RuntimeException because checked exceptions is by far the worst
 * 'feature' of the Java language.
 */
public class MessageQueueFullException extends RuntimeException {

    public MessageQueueFullException() {
        super();
    }

    public MessageQueueFullException(String message) {
        super("MessageQueueFullException: " + message);
    }

}
