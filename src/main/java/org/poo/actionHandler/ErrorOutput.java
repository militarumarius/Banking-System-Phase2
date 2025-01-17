package org.poo.actionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorOutput {
    private final String description;
    private final int timestamp;
    public ErrorOutput(final String description, final int timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }

    /**
     * method that make an ObjectNode for the output
     * @return the ObjectNode created
     */
    public ObjectNode toObjectNodeDescription() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("timestamp", this.timestamp);
        objectNode.put("description", this.description);
        return objectNode;
    }
    /**
     * method that make an ObjectNode for the output, without the timestamp
     * @return the ObjectNode created
     */
    public ObjectNode toObjectNodeErrorWithoutTimestamp() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("error", this.description);
        return objectNode;
    }
    /**
     * method that make an ObjectNode for the output, with the timestamp
     * @return the ObjectNode created
     */
    public ObjectNode toObjectNodeErrorWithTimestamp() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("error", this.description);
        objectNode.put("timestamp", this.timestamp);
        return objectNode;
    }
    /**
     * method that make an ObjectNode for the output, without a success description
     * @return the ObjectNode created
     */
    public ObjectNode toObjectNodeSuccess() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("success", this.description);
        objectNode.put("timestamp", this.timestamp);
        return objectNode;
    }
}
