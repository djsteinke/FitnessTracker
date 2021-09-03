package com.rn5.fitnesstracker.strava;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StravaActivity {
    private final long id;
    private Integer pss;
    private Integer hrss;
    private Long date;
    private String activityType;
    private Integer movingTime;
    private Double distance;
    private Integer hrAvg;
    private Integer pwrAvg;
    private Integer pwrFtp;

    public StravaActivity(long id) {
        this.id = id;
    }
    public StravaActivity withPss(Integer ftpEffort) {
        this.pss = ftpEffort;
        return this;
    }
    public StravaActivity withHrss(Integer hrEffort) {
        this.hrss = hrEffort;
        return this;
    }
    public StravaActivity withDate(Long date) {
        this.date = date;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StravaActivity)) {
            return false;
        }
        StravaActivity c = (StravaActivity) o;
        return id == c.id;
    }
}
