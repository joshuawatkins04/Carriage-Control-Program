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
    private Long timestamp;
    private String status;
    private String station;
    @JsonProperty("door_direction")
    private String doorDirection;
    
    public GetMessageInfo() {}

    public GetMessageInfo(String clientType, String message, String clientID, Long timestamp, String status, String station, String doorDirection) {
        this.clientType = clientType;
        this.message = message;
        this.clientID = clientID;
        this.timestamp = timestamp;
        this.status = status;
        this.station = station;
        this.doorDirection = doorDirection;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getDoorDirection() {
        return doorDirection;
    }

    public void setDoorDirection(String doorDirection) {
        this.doorDirection = doorDirection;
    }
}
