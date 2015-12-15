package org.bajetii.messageserver.server.events;


import java.util.Map;

/**
 * ReadEvent is a simple event.
 */
public class ReadEvent implements IEvent {

    private String eventType;

    private Map<String, String> meta;

    public ReadEvent(Map<String, String> meta, String type) {
        this.meta = meta;
        this.eventType = type;
    }

    public String getEventType() {
        return this.eventType;
    }

    public Map<String, String> getEventMetadata() {
        return this.meta;
    }

    public String getEventPayload() {
        // NOTE: a read event never has a payload:
        return "";
    }
    
}
