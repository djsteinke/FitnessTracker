package com.rn5.fitnesstracker.strava;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StravaActivity {
    private final long id;
    private Integer ftpEffort;
    private Integer hrEffort;
    private Long date;
    private String activityType;
    private Integer movingTime;
    private Double distance;
    private Integer avgHr;
    private Integer avgPwr;
    private Integer avg20Pwr;

    public StravaActivity(long id) {
        this.id = id;
    }
    public StravaActivity withFtpEffort(Integer ftpEffort) {
        this.ftpEffort = ftpEffort;
        return this;
    }
    public StravaActivity withHrEffort(Integer hrEffort) {
        this.hrEffort = hrEffort;
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
