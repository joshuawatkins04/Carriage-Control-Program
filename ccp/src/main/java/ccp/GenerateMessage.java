package ccp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateMessage {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String ccp = "CCP";
    private static String brID = "BR06";
    private static int sequenceNumber = 1000;

    public static String generateInitiationMessage() {
        GetMessageInfo message = new GetMessageInfo();
        
        message.setClientType(ccp);
        message.setMessage("CCIN");
        message.setClientID(brID);
        message.setSequenceNumber(sequenceNumber);

        return convertToJson(message);
    }

    private static String convertToJson(GetMessageInfo message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}