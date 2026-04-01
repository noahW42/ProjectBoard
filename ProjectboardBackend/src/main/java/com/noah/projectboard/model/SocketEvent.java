package com.noah.projectboard.websocket; 

public class SocketEvent {
    private String type;
    private Object data;

    public SocketEvent() {} // default constructor for serialization

    public SocketEvent(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    // getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}