package org.bajetii.messageserver.server.queues;


import java.lang.Runnable;
import java.lang.System;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Date;

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
     * @param   cleanupPeriod   the period in Seconds cleanup() is called.
     **/
    public TopicMessageQueue(int cleanupPeriod) {
        super();
        this.cleanupPeriod = cleanupPeriod;

        // now; launch the Thread that will handle the queue cleanup:
        (new Thread(new CleanupRunnable(this, this.cleanupPeriod))).start();
    }

    /**
     * addMessage adds the given IMessage to the queue.
     * <p>
     * Execution falls under the monitor lock to ensure thread-safety.
     * @throws  MessageQueueInvalidMessageException if the provided IMessage is
     *          not a subclass of TopicMessage.
     */
    @Override
    public synchronized void addMessage(IMessage message) {
        if(!(message instanceof TopicMessage)) {
            throw new MessageQueueInvalidMessageException();
        }

        super.addMessage(message);
    }

    /**
     * getMessage returns the first element in the queue.
     * <p>
     * This method is identical to that of the base MessagingQueue class; we
     * must however explicitly implement it so as to place the method under the
     * monitor lock for objects of this class.
     * @throws  MessageQueueEmptyException  if the MessageQueue has no messages
     *
     * @return  IMessage    the first IMessage in the MessageQueue.
     *          In this particular case; the returned IMessages are guaranteed
     *          to be of type TopicMessage.
     */
    @Override
    public synchronized IMessage getMessage() {
        return super.getMessage();
    }

    /**
     * getMessages returns the all the messages currently in the queue.
     * <p>
     * This method is identical to that of the base MessagingQueue class; we
     * must however explicitly implement it so as to place the method under the
     * monitor lock for objects of this class.
     *
     * @return  IMessage[]  the Array of all messages contained in the queue.
     *          In this particular case; the returned IMessages are guaranteed
     *          to be of type TopicMessage.
     */
    public synchronized IMessage[] getMessages() {
        return super.getMessages();
    }

    /**
     * cleanup goes ahead and cleans up all the expired
     * messages found in the queue.
     * <p>
     * This method is destructive; and thus it can only be called
     * under the protection of the monitor lock.
     *
     * @throws  MessageQueueInvalidMessageException in the vague case that any
     *          of the messages contained in the queue are not of type
     *          TopicMessage.
     */
    @Override
    public synchronized void cleanup() {
        ArrayList<IMessage> remainingMessages = new ArrayList<IMessage>();

        for(IMessage message : this.messages) {
            if(!(message instanceof TopicMessage)) {
                throw new MessageQueueInvalidMessageException();
            }

            TopicMessage topicMessage = (TopicMessage) message;
            
            if(!((new Date(System.currentTimeMillis())).after(topicMessage.getTimeout()))) {
                remainingMessages.add(message);
            }
        }

        this.messages = remainingMessages;
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
