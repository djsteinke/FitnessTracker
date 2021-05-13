package com.rn5.fitnesstracker.model;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Data;

@Data
public class StravaActivity {
    private Long id;
    private Integer ftpEffort;
    private Integer hrEffort;
    private Long date;

    public StravaActivity() {}

    public StravaActivity(Long id, Integer ftpEffort, Integer hrEffort, Long date) {
        this.id = id;
        this.ftpEffort = ftpEffort;
        this.hrEffort = hrEffort;
        this.date = date;
    }

    public StravaActivity(JSONObject object) throws JSONException {
        this.id = object.getLong("id");
        this.ftpEffort = object.getInt("ftpEffort");
        this.hrEffort = object.getInt("hrEffort");
        this.date = Long.parseLong(object.getString("date"));
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id",id);
        object.put("ftpEffort",ftpEffort);
        object.put("hrEffort",hrEffort);
        object.put("date",date);
        return object;
    }
}
