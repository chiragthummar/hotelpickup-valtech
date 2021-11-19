package com.valtech.movenpick.entities;

import android.widget.RadioButton;

import java.io.Serializable;

public class UserSettings implements Serializable {
    private static final long serialVersionUID = -9012379575908319285L;
    private String apiURL;
    private String mode;
    private int isMobileOptional;
    private int cameraToUse;
    private Event[] events;
    private int minimumBarCodeCharacters;
    private String smsAPI;
    private int isNFC;


    public UserSettings() {
        this.apiURL = null;
        this.events = null;
        this.cameraToUse = 0;
        this.minimumBarCodeCharacters = 4;
        this.smsAPI = null;
        this.mode = "free";
    }

    public String getApiURL() {
        return this.apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getNFC() {
        return this.isNFC;
    }

    public void setNFC(int isNFC) {
        this.isNFC = isNFC;
    }


    public int getMobileOptional() {
        return this.isMobileOptional;
    }

    public void setMobileOptional(int isMobileOptional) {
        this.isMobileOptional = isMobileOptional;
    }


    public Event[] getEvents() {
        return this.events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }

    public int getCameraToUse() {
        return this.cameraToUse;
    }

    public void setCameraToUse(int cameraToUse) {
        this.cameraToUse = cameraToUse;
    }

    public int getMinimumBarCodeCharacters() {
        return this.minimumBarCodeCharacters;
    }

    public void setMinimumBarCodeCharacters(int minimumBarCodeCharacters) {
        this.minimumBarCodeCharacters = minimumBarCodeCharacters;
    }

    public String joinedEventIDs() {
        String str = "";
        for (Event event : this.events) {
            if (event.isAllowable()) {
                str = new StringBuilder(String.valueOf(str)).append(event.getEventID()).append(",").toString();
            }
        }
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public String getSmsAPI() {
        return this.smsAPI;
    }

    public void setSmsAPI(String smsAPI) {
        this.smsAPI = smsAPI;
    }
}
