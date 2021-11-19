package com.valtech.movenpick.entities;

import java.io.Serializable;

public class Event implements Serializable {
    private static final long serialVersionUID = 2709465949108268463L;
    private boolean allowable;
    private int eventID;
    private String eventName;

    public Event() {
        this.eventID = 0;
        this.eventName = null;
        this.allowable = false;
    }

    public int getEventID() {
        return this.eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public boolean isAllowable() {
        return this.allowable;
    }

    public void setAllowable(boolean allowable) {
        this.allowable = allowable;
    }
}
