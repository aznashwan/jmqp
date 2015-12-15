package org.bajetii.messageserver.server.messages;


import org.bajetii.messageserver.server.events.IEvent;

/**
 * IMessage represents the properties of the entity which is
 * the main handled object of the MessagingServer.
 *
 * A message is also an event.
 * <p>
 * It provides the usual properties exposed by your average String, whilst
 * allowing for implementing classes to hide the backend data format used for
 * its representation (ex: binary; ascii; unicode etc...)
 */
public interface IMessage extends IEvent {

    /**
     * getStringValue returns the String representation of the IMessage.
     * <p>
     * It is completely analogous to toString, whilst still providing
     * flexibility by not forcing the overriding of toString().
     */
    public String getStringValue();

    /**
     * valuesEqual returns true depending on the whether or not the provided
     * Object implements IMessage and its getStringValue is equal to this
     * objects.
     * <p>
     * It is completely analogous to equals, whilst still providing flexibility
     * by not forcing the overriding of equals().
     */
    public default boolean valuesEqual(Object other) {
        if((other == null) || !(other instanceof IMessage)) {
            return false;
        }

        IMessage otherMessage = (IMessage) other;
        return this.getStringValue() == otherMessage.getStringValue();
    }
}
