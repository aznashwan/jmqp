package org.bajetii.messageserver.server.messages;


/**
 * StringMessage is a simple String wrapper which
 * satisfies the IMessage interface.
 * <p>
 * It simply stores the given String inside a field.
 */
public class StringMessage extends Message implements IMessage {

    /**
     * string is the actual string of the message.
     */
    protected String string;

    /**
     * A StringMessage is created provided its contained String.
     * <p>
     * @param   message the message String to be encapsulated.
     */
    public StringMessage(String message) {
        this.string = message;
    }

    /**
     * getStringValue simply returns the encapsulated String.
     */
    @Override
    public String getStringValue() {
        return this.string;
    }

}
