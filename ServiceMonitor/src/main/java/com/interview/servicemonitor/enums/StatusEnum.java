package com.interview.servicemonitor.enums;

/**
 * @author manuja
 * Name : StatusEnum
 * To Identify the current status of the service
 * And Show the Status in user interface
 */
public enum StatusEnum {
    UP(1,"Server is Up"),
    DOWN(2,"Server is Down"),
    OUTAGE(3,"Server is on Outage Timer"),
    TERMINATED(4,"Server is Terminated");
    
    private int id ;
    private String status;
    
    private StatusEnum(int id , String status) {
        this.id=id;
        this.status=status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
