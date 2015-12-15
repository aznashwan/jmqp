package org.bajetii.messageserver.server.queues;


import java.lang.Runnable;
import java.lang.System;
import java.lang.Thread;
import java.util.Date;

import org.bajetii.messageserver.server.events.IEvent;
import org.bajetii.messageserver.server.messages.IMessage;
import org.bajetii.messageserver.server.messages.TopicMessage;
import org.bajetii.messageserver.server.queues.exceptions.MessageQueueInvalidMessageException;


/**
 * TopicMessageQueue is a specific type of queue which only holds IMessages of
 * type TopicMessage.
 * <p>
 * The messages are persistent up until their timeout moment has come; upon
 * which they get asynchronously garbage collected in a thread-safe manner.
 */
public class TopicMessageQueue extends MessageQueue {

    /**
     * cleanupPeriod represents the period in Seconds at which
     * the cleanup() method is called.
     */
    private int cleanupPeriod;

    /**
     * A TopicMessageQueue is created provided the number of seconds
     * representing the period that cleanup()s should be issued.
     * <p>
     * @param   to              String name of the object of the Queue.
     * @param   cleanupPeriod   the period in Seconds cleanup() is called.
     **/
    public TopicMessageQueue(String to, int cleanupPeriod) {
        super(to);
        this.cleanupPeriod = cleanupPeriod;

        // now; launch the Thread that will handle the queue cleanup:
        (new Thread(new CleanupRunnable(this, this.cleanupPeriod))).start();
    }

    /**
     * addMessage adds the given IMessage to the queue.
     * <p>
     *
     * @throws  MessageQueueInvalidMessageException if the provided IMessage is
     *          not a subclass of TopicMessage.
     */
    @Override
    public void addMessage(IMessage message) {
        if(!(message instanceof TopicMessage)) {
            throw new MessageQueueInvalidMessageException();
        }

        super.addMessage(message);
    }

    /**
     * addMessage adds the given IEvent as an IMessage.
     */
    public void addMessage(IEvent e) {
        int timeout;
        
        try {
            // NOTE: timeout guaranteed to be present:
            timeout = Integer.parseInt(e.getEventMetadata().get("Timeout"));
        } catch(NumberFormatException ex) {
            // NOTE: badrandom default:
            timeout = 100;
        }
        super.addMessage(new TopicMessage(e.getEventMetadata(), e.getEventPayload(), timeout));
    }

    /**
     * cleanup goes ahead and cleans up all the expired
     * messages found in the queue.
     * <p>
     *
     * @throws  MessageQueueInvalidMessageException in the vague case that any
     *          of the messages contained in the queue are not of type
     *          TopicMessage.
     */
    @Override
    public void cleanup() {
        synchronized(this.messages) {
            for(IMessage message : this.messages) {
                if(!(message instanceof TopicMessage)) {
                    throw new MessageQueueInvalidMessageException();
                }

                TopicMessage topicMessage = (TopicMessage) message;

                if((new Date(System.currentTimeMillis())).after(topicMessage.getTimeout())) {
                    this.messages.remove(message);
                }
            }
        }
    }

    /**
     * CleanupRunnable is an aggregated class which implements Runnable and can be
     * launched asynchronously to handle the cleanup of the queue's messages.
     */
    private class CleanupRunnable implements Runnable {

        /**
         * queue is the TopicMessageQueue whose cleanup this CleanupRunnable
         * will handle.
         */
        private TopicMessageQueue queue;

        /**
         * cleanupPeriod is the period in Seconds between cleanup()s issued
         * on the queue.
         */
        private int cleanupPeriod;

        /**
         * lastCleanupTime saves the moment the last cleanup had occured.
         */
        private Date lastCleanupTime;

        /**
         * A CleanupRunnable is created provided the managed TopicMessageQueue
         * and the cleanup period in Seconds.
         * <p>
         *
         * @param   queue           the monitored TopicMessageQueue.
         * @param   cleanupPeriod   the period in seconds between cleanups.
         */
        public CleanupRunnable(TopicMessageQueue queue, int cleanupPeriod) {
            this.queue = queue;
            this.cleanupPeriod = cleanupPeriod;
            this.lastCleanupTime = new Date(System.currentTimeMillis());
        }

        /**
         * run is the main method to be run asynchronously for this
         * CleanupRunnable.
         * <p>
         * All it does is attempt to aquire the monitor lock associated to its
         * queue; call cleanup() on the queue, notify that the lock has been
         * released and sleep for the given period.
         */
        public void run() {
            while(!(System.currentTimeMillis() > (this.lastCleanupTime.getTime() + 1000 * this.cleanupPeriod))) {
                this.queue.cleanup();
                this.lastCleanupTime = new Date(System.currentTimeMillis());
                continue;
            }
        }
    }
}
