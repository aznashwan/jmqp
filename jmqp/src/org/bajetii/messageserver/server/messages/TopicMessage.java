package org.bajetii.messageserver.server.messages;


import java.util.Date;
import java.util.Map;
import java.lang.System;


/**
 * TopicMessage is a specialized subtype of StringMessage which also
 * contains an associated timeout moment.
 * <p>
 * It implements the IMessage interface
 */
public class TopicMessage extends StringMessage implements IMessage {

    private Date timeout;

    /**
     * A TopicMessage can be created by providing the String message
     * and the desired timeout in Seconds.
     * <p>
     * If the desired timeout is specified in seconds; the timeout moment
     * will be internally computed; which may cause a slight loss of precision.
     *
     * @param   meta    the Map<String, String> of Message metadata.
     * @param   message the message String to be encapsulated.
     * @param   seconds the number of seconds of timeout.
     */
    public TopicMessage(Map<String, String> meta, String message, int seconds) {
        super(meta, message);
        Date date = new Date(System.currentTimeMillis());
        this.timeout = new Date(date.getTime() + 1000 * seconds);
    }

    /**
     * A TopicMessage can be created by providing the String message
     * and the moment of timeout.
     * <p>
     * @param   meta    the Map<String, String> of Message metadata.
     * @param   message the message String to be encapsulated.
     * @param   timeout the Date object representing the moment of timeout.
     */
    public TopicMessage(Map<String, String> meta, String message, Date timeout) {
        super(meta, message);
        this.timeout = timeout;
    }

    /**
     * getTimeout returns the Date object representing the moment
     * this TopicMessage will timeout.
     * <p>
     * @return  Date    Date object representing moment of timeout.
     */
    public Date getTimeout() {
        return this.timeout;
    }

}
