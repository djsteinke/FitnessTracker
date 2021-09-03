package com.rn5.fitnesstracker.athlete.fitness;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ftp {
    int ftp;
    int hr;
    long date;

    public Ftp() {
    }

    public Ftp withFtp(int ftp) {
        this.ftp = ftp;
        return this;
    }

    public Ftp withHr(int hr) {
        this.hr = hr;
        return this;
    }

    public Ftp withDate(long dt) {
        this.date = dt;
        return this;
    }

}
