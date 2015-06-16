package com.visena.test.caldav.milton;

import java.util.Date;

public class Meeting {

    public long id;
    public String name;  // filename for the meeting. Must be unique within the user
    private Date modifiedDate;
    private Date createdDate;
    private byte[] icalData;
    public final Calendar cal;
    public final User user;

    public Meeting(Calendar cal, User user) {
        this.cal = cal;
        this.user = user;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public byte[] getIcalData() {
        return icalData;
    }

    public void setIcalData(byte[] icalData) {
        this.icalData = icalData;
    }
}
