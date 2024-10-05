package ccp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateMessage {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String ccp = "ccp";
    private static String brID = "BR06";

    public static String generateHandshakeMessage(Long timestamp) {
        GetMessageInfo message = new GetMessageInfo();
        
        message.setClientType(ccp);
        message.setMessage("CCIN");
        message.setClientID(brID);
        message.setTimestamp(timestamp);

        return convertToJson(message);
    }

    public static String generateStatusMessage(Long timestamp) {
        GetMessageInfo message = new GetMessageInfo();
        
        message.setClientType(ccp);
        message.setMessage("STAT");
        message.setClientID(brID);
        message.setTimestamp(timestamp);
        message.setStatus("ON"); // need to change

        return convertToJson(message);
    }

    public static String generateStationStatusMessage(Long timestamp) {
        GetMessageInfo message = new GetMessageInfo();
        
        message.setClientType(ccp);
        message.setMessage("STAT");
        message.setClientID(brID);
        message.setTimestamp(timestamp);
        message.setStatus("STOPPED_AT_STATION");
        message.setStation("STXX"); // Needs to grab StationID

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
