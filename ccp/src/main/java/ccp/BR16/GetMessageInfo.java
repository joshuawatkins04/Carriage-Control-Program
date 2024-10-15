package ccp.BR16;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMessageInfo {

    private String clientType;
    private String message;
    private String clientID;
    private int sequenceNumber;
    private String action;
    private String status;
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
}
