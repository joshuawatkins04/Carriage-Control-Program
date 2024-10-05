package ccp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMessageInfo {

    @JsonProperty("client_type")
    private String clientType;
    private String message;
    @JsonProperty("client_id")
    private String clientID;
    private int sequenceNumber;
    private String action;
    // private Long timestamp;
    private String status;
    // private String station;
    // @JsonProperty("door_direction")
    // private String doorDirection;
    private String brID;
    
    public GetMessageInfo() {}

    public GetMessageInfo(String clientType, String message, String clientID, int sequenceNumber, String action, String status, String brID) {
        this.clientType = clientType;
        this.message = message;
        this.clientID = clientID;
        this.sequenceNumber = sequenceNumber;
        this.action = action;
        this.status = status;
        this.brID = brID;
        // this.station = station;
        // this.doorDirection = doorDirection;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBrID() {
        return brID;
    }

    public void setBrID(String brID) {
        this.brID = brID;
    }

    // public Long getTimestamp() {
    //     return timestamp;
    // }

    // public void setTimestamp(Long timestamp) {
    //     this.timestamp = timestamp;
    // }

    // public String getStation() {
    //     return station;
    // }

    // public void setStation(String station) {
    //     this.station = station;
    // }

    // public String getDoorDirection() {
    //     return doorDirection;
    // }

    // public void setDoorDirection(String doorDirection) {
    //     this.doorDirection = doorDirection;
    // }
}
