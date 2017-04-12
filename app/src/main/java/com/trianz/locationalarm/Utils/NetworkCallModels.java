package com.trianz.locationalarm.Utils;

import org.json.JSONObject;

/**
 * Created by Rakshitha.Krishnayya on 12-04-2017.
 */

public class NetworkCallModels {
    private JSONObject json;

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public Boolean status;
    public String message;
}
