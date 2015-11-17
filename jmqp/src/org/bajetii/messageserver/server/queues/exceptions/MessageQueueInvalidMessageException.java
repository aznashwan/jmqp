package org.bajetii.messageserver.server.queues.exceptions;


/**
 * MessageQueueInvalidMessageTypeException is thrown when a Message of an
 * invalid type is attempted to be inserted within a specific MessageQueue.
 * <p>
 * It extends RuntimeException to work around the amazing 'feature'
 * which is Java's checked exceptions.
 */
public class MessageQueueInvalidMessageException extends RuntimeException {

    public MessageQueueInvalidMessageException() {
        super();
    }

    public MessageQueueInvalidMessageException(String message) {
        super("MessageQueueInvalidMessageException: " + message);
    }

}
