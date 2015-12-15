package org.bajetii.messageserver.server.events;

public interface IEventHandler {

    /**
     * handleEvent handles the given event.
     */
    public String handleEvent(IEvent e);

    /**
     * cares returns true if the EventHandler cares about the given Event.
     */
    public boolean cares(IEvent e);

}
