package org.bajetii.messageserver.server.messages;

/**
 * Message is a base abstract implementation of IMessage.
 * <p>
 * It provides the added benefits of defaulting equals and toString to
 * IMessage's valuesEqual() and getStringValue()
 */
public abstract class Message implements IMessage {

    public abstract String getStringValue();

    public String toString() {
        return this.getStringValue();
    }

    public boolean equals(Object other) {
        return this.valuesEqual(other);
    }

}
