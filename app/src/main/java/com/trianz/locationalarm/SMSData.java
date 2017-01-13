package com.trianz.locationalarm;

/**
 * Created by Rakshitha.Krishnayya on 11-01-2017.
 */

public class SMSData {

    // Number from witch the sms was send
    private String number;
    // SMS text body
    private String body;

    private String name;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
