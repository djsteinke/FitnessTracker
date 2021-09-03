package com.rn5.fitnesstracker.athlete.detail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AthleteDetail implements Comparable<AthleteDetail> {

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
    public int compareTo(AthleteDetail o1) {
        return Long.compare(this.id, o1.getId());
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
