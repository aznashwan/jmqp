package org.bajetii.messageserver.server.exceptions;


/**
 * MessageServerPersonNotFoundException is thrown whenever a person tries to
 * read messages but there are none.
 * <p>
 * It extends RuntimeException to avoid Java's amazing checked exceptions.
 */
public class MessageServerPersonNotFoundException extends RuntimeException {

    public MessageServerPersonNotFoundException() {
        super();
    }

    public MessageServerPersonNotFoundException(String message) {
        super("Unable to find any messages for person: '" + message + "'.");
    }

}
