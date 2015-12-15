package org.bajetii.messageserver.server.events;


/**
 * IEventDispatcher is the interface of event dispatcher.
 */
public interface IEventDispatcher {
    
    /**
     * dispatchEvent takes an IEvent, dispatches its execution and returns the
     * String response.
     * <p>
     * @param   IEvent  the IEvent to be dispatched
     * @return  String  the String response of the IEvent's processing
     */
    public String dispatchEvent(IEvent e);

}
