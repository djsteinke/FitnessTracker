package com.rn5.fitnesstracker.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AthleteDetail {

    private long date;
    private int ftp;
    private int hrm;
    private int hrr;

    public AthleteDetail() {}

    public AthleteDetail(int ftp, int hrm, int hrr, long date) {
        this.ftp = ftp;
        this.hrm = hrm;
        this.hrr = hrr;
        this.date = date;
    }

    public AthleteDetail(JSONObject object) throws JSONException {
        this.ftp = object.getInt("ftp");
        this.hrm = object.getInt("hrm");
        this.hrr = object.getInt("hrr");
        this.date = Long.parseLong(object.getString("date"));
    }

    public JSONObject toJson() throws JSONException {
        Gson gson = new GsonBuilder().create();
        String val = gson.toJson(this);
        return new JSONObject(val);
    }
}
