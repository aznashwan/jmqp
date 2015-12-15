package org.bajetii.messageserver.server.events;

import java.util.Map;

/*
 * IEvent is the interface of all events the server may experience.
 * The type of these events is simply encoded as a String returned by
 * getEventType. The event's "payload" is returned as the simple String
 * message.
 */
public interface IEvent {

    /*
     * getEventType returns the type of the event.
     */
    public String getEventType();

    /*
     * getEventMetadata returns the key-value mapping of metadata
     * associated to the event:
     */
    public Map<String, String> getEventMetadata();

    /*
     * getEventPayload returns the String "payload" of the event:
     */
    public String getEventPayload();

}
