package com.rn5.fitnesstracker.model;

import com.rn5.fitnesstracker.define.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import lombok.Data;

@Data
public class Fitness {
    private Integer stressScore;
    private Integer hrStressScore;
    private Double fitness;
    private Double fatigue;
    private Double form;
    private Long date;
    private Long id;

    public Fitness() {}

    public Fitness(Integer stressScore, Integer hrStressScore, Double fitness, Double fatigue, Double form, Long date) {
        this.stressScore = stressScore;
        this.hrStressScore = hrStressScore;
        this.fitness = fitness;
        this.fatigue = fatigue;
        this.form = form;
        this.date = date;
    }

    public Fitness(JSONObject object) throws JSONException {
        this.stressScore = Json.getJSONInt(object,"stressScore",0);
        this.hrStressScore = Json.getJSONInt(object,"hrStressScore",0);
        this.fitness = Json.getJSONDouble(object,"fitness",0d);
        this.fatigue = Json.getJSONDouble(object,"fatigue",0d);
        this.form = Json.getJSONDouble(object,"form",0d);
        this.date = Json.getJSONLong(object,"date",0L);
        this.id = Json.getJSONLong(object,"id",0L);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("hrStressScore",hrStressScore);
        object.put("stressScore",stressScore);
        object.put("fitness",fitness);
        object.put("fatigue",fatigue);
        object.put("form",form);
        object.put("date",date);
        object.put("id",id);
        return object;
    }

}
