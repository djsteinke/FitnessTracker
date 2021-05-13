package com.rn5.libstrava.common.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.TimeZone;

import lombok.Data;

@Data
public class Token {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long expirationDate;
    private String username;
    private String firstName;
    private String lastName;

    public Token withAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
    public Token withTokenType(String type) {
        this.tokenType = type;
        return this;
    }
    public Token withRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
    public Token withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    public Token withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    public Token withUsername(String username) {
        this.username = username;
        return this;
    }

    public Token expiresAt(Long expiresAt) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        now.setTimeInMillis(expiresAt);
        this.expirationDate = now.getTimeInMillis();
        return this;
    }

    public Token() {}

    public Token(String value) {
        String tmpVal = value;
        if (value.startsWith("Bearer"))
            tmpVal = value.substring(7);
        this.accessToken = tmpVal;
    }

    @Override
    public String toString() {
        return tokenType + " " + accessToken;
    }

    public void setExpirationDate(Long expiresAt) {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        now.setTimeInMillis(expiresAt*1000L);
        this.expirationDate = now.getTimeInMillis();
    }
}
