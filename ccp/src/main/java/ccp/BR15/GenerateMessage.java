package ccp.BR15;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateMessage {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String ccp = "CCP";
    private static String brID = "BR06";
    private static int sequenceNumber = 1000;

    public static String generateInitiationMessage() {
        return generateMessage("CCIN", null);
    }

    public static String generateStatusMessage(String status) {
        return generateMessage("STAT", status);
    }

    public static String generateAckMessage() {
        return generateMessage("AKEX", null);
    }

    private static String generateMessage(String messageType, String status) {
        GetMessageInfo message = new GetMessageInfo();
        message.setClientType(ccp);
        message.setMessage(messageType);
        message.setClientID(brID);
        message.setSequenceNumber(incrementSequenceNumber());
        message.setStatus(status);
        return convertToJson(message);
    }

    private static int incrementSequenceNumber() {
        return sequenceNumber++;
    }

    private static String convertToJson(GetMessageInfo message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error with message";
        }
    }
}