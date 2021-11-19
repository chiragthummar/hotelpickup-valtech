package com.valtech.movenpick.entities;

import java.io.Serializable;

public class JammedMessage implements Serializable {
    private static final long serialVersionUID = 2551467572572086199L;
    private String barCode;
    private String eta;
    private int eventID;
    private String jammedMessage;

    public JammedMessage() {
        this.eventID = 0;
        this.jammedMessage = null;
        this.eta = null;
        this.barCode = null;
    }

    public int getEventID() {
        return this.eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getJammedMessage() {
        return this.jammedMessage;
    }

    public void setJammedMessage(String jammedMessage) {
        this.jammedMessage = jammedMessage;
    }

    public String getEta() {
        return this.eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getBarCode() {
        return this.barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
