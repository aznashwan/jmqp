package org.bajetii.messageserver.server.messages;


import java.util.Map;


/**
 * StringMessage is a simple String wrapper which
 * satisfies the IMessage interface.
 *
 * It also satisfies the IEvent interface at any message has associated data:
 * <p>
 * It simply stores the given String inside a field.
 */
public class StringMessage extends Message {

    /**
     * string is the actual string of the message.
     */
    protected String string;

    /**
     * metadata is the key-value mapping of metadata associated to the message.
     */
    protected Map<String, String> meta;

    /**
     * A StringMessage is created provided its contained String.
     * <p>
     * @param   meta    the Map<String, String> of Message metadata.
     * @param   message the message String to be encapsulated.
     */
    public StringMessage(Map<String, String> meta, String message) {
        this.meta = meta;
        this.string = message;
    }

    /**
     * getStringValue simply returns the encapsulated String.
     */
    @Override
    public String getStringValue() {
        return this.string;
    }

    /** getEventType returns the String type of the Message.
     * <p>
     * @return  String  type of the event.
     */
    public String getEventType() {
        // NOTE: 'Type' guaranteed to be present by http handlers:
        return this.meta.get("Type");
    }

    /**
     * getEventPayload returns the String payload of the Event.
     * <p>
     * @return  String  the String payload of the event.
     */
    public String getEventPayload() {
        return this.getStringValue();
    }

    /**
     * getEventMetada returns the mapping representing the metadata
     * of the Event.
     * <p>
     * @return  Map<String, String> the mapping of metadata of the Event.
     */
    public Map<String, String> getEventMetadata() {
        return this.meta;
    }

}
