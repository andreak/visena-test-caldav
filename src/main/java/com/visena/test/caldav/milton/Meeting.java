package com.visena.test.caldav.milton;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.resource.DeletableResource;

import java.util.Date;

public class Meeting implements DeletableResource {

    private long id;
    private String name;  // filename for the meeting. Must be unique within the user
    private Date modifiedDate;
    private Date createdDate;
    private byte[] icalData;
    public final Calendar cal;

    public Meeting(Calendar cal) {
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
        System.out.println("Deleting " + getName());
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(getId());
    }

    @Override
    public Object authenticate(String user, String password) {
        return null;
    }

    @Override
    public boolean authorise(Request request, Request.Method method, Auth auth) {
        return false;
    }

    @Override
    public String getRealm() {
        return null;
    }

    @Override
    public String checkRedirect(Request request) throws NotAuthorizedException, BadRequestException {
        return null;
    }
}
