package ccp.BR15;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMessageInfo15 {

    private String client_type;
    private String message;
    private String client_id;
    private int sequence_number;
    private String action;
    private String status;
    private String brID;
    
    public GetMessageInfo15() {}

    public GetMessageInfo15(String client_type, String message, String client_id, int sequence_number, String action, String status, String brID) {
        this.client_type = client_type;
        this.message = message;
        this.client_id = client_id;
        this.sequence_number = sequence_number;
        this.action = action;
        this.status = status;
        this.brID = brID;
    }

    public String getClientType() {
        return client_type;
    }

    public void setClientType(String client_type) {
        this.client_type = client_type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientID() {
        return client_id;
    }

    public void setClientID(String client_id) {
        this.client_id = client_id;
    }

    public int getSequenceNumber() {
        return sequence_number;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequence_number = sequenceNumber;
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
