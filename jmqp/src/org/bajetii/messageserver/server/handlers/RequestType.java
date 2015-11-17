package org.bajetii.messageserver.server.handlers;

/**
 * RequestType is the simple Enum representing the two types of request.
 */
public enum RequestType {

    /**
     * A request can either be for accessing Personal messages; or the topics:
     */
    TOPIC("Topic"), PERSONAL("Personal");

    /**
     * value is the stored String value of the RequestType.
     */
    private final String value;

    /**
     * A RequestType can be constructed from the two String
     * representations of its allowed values.
     */
    private RequestType(String value) {
        this.value = value;
    }
}
