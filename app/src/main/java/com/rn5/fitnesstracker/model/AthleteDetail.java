package com.rn5.fitnesstracker.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthleteDetail {

    private long id;
    private long date;
    private int ftp;
    private int hrm;
    private int hrr;
    private boolean auto = true;

    public AthleteDetail() {}

    public AthleteDetail(int ftp, int hrm, int hrr, long date, long id) {
        this.id = id;
        this.ftp = ftp;
        this.hrm = hrm;
        this.hrr = hrr;
        this.date = date;
    }
    public AthleteDetail isAuto(boolean auto) {
        this.auto = auto;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AthleteDetail)) {
            return false;
        }
        AthleteDetail c = (AthleteDetail) o;
        return id == c.id;
    }
}
