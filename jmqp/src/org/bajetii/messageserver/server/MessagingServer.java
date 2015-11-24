package org.bajetii.messageserver.server;


import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.bajetii.messageserver.server.exceptions.MessageServerPersonNotFoundException;
import org.bajetii.messageserver.server.exceptions.MessageServerTopicNotFoundException;
import org.bajetii.messageserver.server.messages.IMessage;
import org.bajetii.messageserver.server.messages.StringMessage;
import org.bajetii.messageserver.server.messages.TopicMessage;
import org.bajetii.messageserver.server.queues.IMessageQueue;
import org.bajetii.messageserver.server.queues.PersonalMessageQueue;
import org.bajetii.messageserver.server.queues.TopicMessageQueue;


/**
 * MessagingServer is the class of the main messaging server.
 * <p>
 * All of the hazardous methods performed by the server are performed under the
 * protection of their respective monitor locks.
 */
public class MessagingServer {

    /**
     * maxServerTimeout is the maximum timeout in Seconds that
     * topic messages are allowed.
     * <p>
     * It overrides any later settings which may or may
     * not come from within the header of the message itself.
     */
    private int maxServerTimeout;

    /**
     * personalQueuesLock is the Lock which guards access to the
     * map of personal queues.
     */
    private ReentrantLock personalQueuesLock = new ReentrantLock();

    /**
     * personalQueues is a simple association between the String names of
     * people which use the messaging server an their respective queue.
     */
    private HashMap<String, IMessageQueue> personalQueues = new HashMap<String, IMessageQueue>();

    /**
     * topicQueuesLock is the Lock which guards access to the
     * map of topic queues.
     */
    private ReentrantLock topicQueuesLock = new ReentrantLock();

    /**
     * topicQueues is a simple mapping between the String titles of topics and
     * the the messaging queue associated to that respective topic.
     */
    private HashMap<String, IMessageQueue> topicQueues = new HashMap<String, IMessageQueue>();


    /**
     * A messaging server is created given...
     * TODO: maxTimeout, port
     */
    public MessagingServer(int maxServerTimeout) {
        this.maxServerTimeout = maxServerTimeout;
    }

    /**
     * getMaxServerTimeout returns the value of the maximum topic message
     * timeout on this server.
     * <p>
     * @return  int     value in seconds of the maximum allowed timeout.
     * 
     */
    public int getMaxServerTimeout() {
        return this.maxServerTimeout;
    }

    /**
     * addPersonalQueue creates a new entry in the personalQueues HashMap
     * with the given name and an empty PersonalMessageQueue.
     * <p>
     *
     * @param   name    the name of the person the new MessagingQueue is for.
     */
    public void addPersonalQueue(String name) {
        this.personalQueuesLock.lock();
        this.personalQueues.put(name, new PersonalMessageQueue(10)); // NOTE
        this.personalQueuesLock.unlock();
    }

    /**
     * addTopicQueue creates a new entry in the topicQueues HashMap with
     * the given name and an empty TopicMessageQueue.
     */
    public void addTopicQueue(String topic) {
        this.topicQueuesLock.lock();
        this.topicQueues.put(topic, new TopicMessageQueue(5)); // NOTE
        this.topicQueuesLock.unlock();
    }

    /**
     * addPersonalMessage adds a new message inside the queue of the specified
     * person's name for later reading.
     * <p>
     * If the person does not have a queue yet; one will be created for him.
     *
     * @param   person  String name of the recipient of the message.
     * @param   message String contents of the message to be sent.
     */
    public void addPersonalMessage(String person, String message) {
        if(this.personalQueues.get(person) == null) {
            this.addPersonalQueue(person);
        }

        this.personalQueues.get(person).addMessage(new StringMessage(message));
    }

     /**
     * addTopicMessage adds a new message inside the queue of the specified
     * topic's name for later reading.
     * <p>
     * If a queue for that topic does not exist already; one will be
     * created especially for it.
     * If the message's timeout is larger than the server's maximum configured
     * timeout value; it will default to the server's limit.
     *
     * @param   topic   String name of the topic for the message.
     * @param   message String contents of the message to be sent.
     * @param   timeout int representing the timeout of the message.
     */
    public void addTopicMessage(String topic, String message, int timeout) {
        // first; choose the smaller limit between the one set on the server
        // and the one that came with the message.
        // NOTE: LOLs @ ternary operator.
        int tout = (this.maxServerTimeout < timeout) ? this.maxServerTimeout : timeout;

        this.topicQueuesLock.lock();
        if(this.topicQueues.get(topic) == null) {
            this.addTopicQueue(topic);
        }
        this.topicQueues.get(topic).addMessage(
            new TopicMessage(message, tout)
        );
        this.topicQueuesLock.unlock();
    }

    /**
     * getPersonalMessage returns the first message from the queue for the
     * specified recipient's name.
     * <p>
     * @param   person  the name of the person requesting a message.
     * @return  String  the first String message in the person's message queue.
     */
    public String getPersonalMessage(String person) {
        this.personalQueuesLock.lock();
        if(this.personalQueues.get(person) == null) {
            throw new MessageServerPersonNotFoundException(person);
        }

        String message = this.personalQueues.get(person).getMessage().getStringValue();
        this.personalQueuesLock.unlock();

        return message;
    }

    /**
     * getPersonalMessage returns all the messages from the queue for the
     * specified recipient's name.
     * <p>
     * @param   person      the name of the person requesting the messages.
     * @return  String[]    the list of all the messages on the server.
     */
    public String[] getPersonalMessages(String person) {
        this.personalQueuesLock.lock();
        if(this.personalQueues.get(person) == null) {
            throw new MessageServerPersonNotFoundException(person);
        }

        IMessage[] messages = this.personalQueues.get(person).getMessages();
        String[] result = new String[messages.length];

        int i;
        for(i = 0; i < messages.length; i++) {
            result[i] = messages[i].getStringValue();
        }
        this.personalQueuesLock.unlock();

        return result;
    }

    /**
     * getTopicMessage returns the first message from the queue under
     * the specified topic.
     * <p>
     * @param   topic   the name of the topic requesting a message.
     * @return  String  the first String message in the topic's queue.
     * @throws  MessageServerTopicNotFoundException
     */
    public String getTopicMessage(String topic) {
        this.topicQueuesLock.lock();
        if(this.topicQueues.get(topic) == null) {
            throw new MessageServerTopicNotFoundException(topic);
        }

        String message = this.topicQueues.get(topic).getMessage().getStringValue();
        this.topicQueuesLock.unlock();

        return message;
    }

    /**
     * getTopicMessages returns all the messages from the queue under the
     * specified topic.
     * <p>
     * @param   topic       the name of the topic requesting the messages.
     * @return  String[]    the list of all the messages on the server.
     */
    public String[] getTopicMessages(String topic) {
        this.topicQueuesLock.lock();
        if(this.topicQueues.get(topic) == null) {
            throw new MessageServerTopicNotFoundException(topic);
        }

        IMessage[] messages = this.topicQueues.get(topic).getMessages();
        String[] result = new String[messages.length];

        int i;
        for(i = 0; i < messages.length; i++) {
            result[i] = messages[i].getStringValue();
        }
        this.topicQueuesLock.unlock();

        return result;
    }

}
