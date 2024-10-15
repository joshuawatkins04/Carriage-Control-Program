package ccp.BR15;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class GenerateMessage15 {
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private static String ccp = "CCP";
    private static String brID = "BR15";
    private static int sequence_number = 1000;

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
        GetMessageInfo15 message = new GetMessageInfo15();
        message.setClientType(ccp);
        message.setMessage(messageType);
        message.setClientID(brID);
        message.setSequenceNumber(incrementSequenceNumber());
        message.setStatus(status);
        return convertToJson(message);
    }

    private static int incrementSequenceNumber() {
        return sequence_number++;
    }

    private static String convertToJson(GetMessageInfo15 message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error with message";
        }
    }
}