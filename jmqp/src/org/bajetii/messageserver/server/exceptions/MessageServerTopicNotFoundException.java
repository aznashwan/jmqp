package org.bajetii.messageserver.server.exceptions;


/**
 * MessageServerTopicNotFoundException is thrown whenever a request for the
 * messages of a nonexistent topic are recieved.
 * <p>
 * It inherits from RuntimeException so as to avoid Java's
 * amazing checked exception feature.
 */
public class MessageServerTopicNotFoundException extends RuntimeException {

    public MessageServerTopicNotFoundException() {
        super();
    }

    public MessageServerTopicNotFoundException(String message) {
        super("Unable to find any messages for topic: '" + message + "'.");
    }

}
