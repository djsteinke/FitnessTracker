package com.rn5.fitnesstracker.strava;

import android.util.Log;

import com.rn5.fitnesstracker.util.Json;
import com.rn5.libstrava.common.model.Token;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import lombok.Data;

import static com.rn5.fitnesstracker.util.Constants.APP_FILE_PATH;
import static com.rn5.libstrava.common.model.Constants.TOKEN;

@Data
public class StravaToken {

    private static String FILE_NAME = "token.json";

    public StravaToken() {}

    public void update() {
        if (TOKEN != null) {
            save();
        } else {
            delete();
        }
    }

    private void delete() {
        File fileToken = new File(APP_FILE_PATH,FILE_NAME);
        if (fileToken.exists())
            //noinspection ResultOfMethodCallIgnored
            fileToken.delete();
    }

    private void save() {
        File fileToken = new File(APP_FILE_PATH,FILE_NAME);

        try {
            JSONObject jsonToken = new JSONObject();
            jsonToken.put("tokenType",TOKEN.getTokenType());
            jsonToken.put("accessToken",TOKEN.getAccessToken());
            jsonToken.put("refreshToken",TOKEN.getRefreshToken());
            jsonToken.put("username",TOKEN.getUsername());
            jsonToken.put("expirationDate",TOKEN.getExpirationDate());
            jsonToken.put("firstName",TOKEN.getFirstName());
            jsonToken.put("lastName",TOKEN.getLastName());
            FileWriter fw = new FileWriter(fileToken);
            fw.write(jsonToken.toString());
            fw.close();
        } catch (Exception e) {
            Log.e("Token","SAVE ERROR : " + e.getMessage());
        }
    }

    public StravaToken fromFile() {
        File fileToken = new File(APP_FILE_PATH,FILE_NAME);
        StringBuilder sb = new StringBuilder();
        String ln;

        try {
            if (fileToken.exists()) {
                FileReader fr = new FileReader(fileToken);
                BufferedReader br = new BufferedReader(fr);
                while ((ln = br.readLine()) != null) sb.append(ln);
                br.close();
                fr.close();

                JSONObject jsonToken = new JSONObject(sb.toString());
                TOKEN = new Token()
                        .expiresAt(Json.getJSONLong(jsonToken,"expirationDate",null));
                TOKEN.setTokenType(Json.getJSONString(jsonToken,"tokenType",null));
                TOKEN.setAccessToken(Json.getJSONString(jsonToken,"accessToken",null));
                TOKEN.setRefreshToken(Json.getJSONString(jsonToken,"refreshToken",null));
                TOKEN.setUsername(Json.getJSONString(jsonToken,"username",null));
                //TOKEN.setExpirationDate(Json.getJSONLong(jsonToken,"expirationDate",null));
                TOKEN.setFirstName(Json.getJSONString(jsonToken,"firstName",null));
                TOKEN.setLastName(Json.getJSONString(jsonToken,"lastName",null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }
}
