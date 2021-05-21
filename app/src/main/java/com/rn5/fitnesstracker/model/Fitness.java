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
    private long id;

    public Fitness() {}

    public Fitness(Integer stressScore, Integer hrStressScore, Double fitness, Double fatigue, Double form, Long date) {
        this.stressScore = stressScore;
        this.hrStressScore = hrStressScore;
        this.fitness = fitness;
        this.fatigue = fatigue;
        this.form = form;
        this.date = date;
        this.id = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Fitness)) {
            return false;
        }
        Fitness c = (Fitness) o;
        return id == c.id;
    }

}
