package com.rn5.fitnesstracker.async;

import android.os.AsyncTask;
import android.util.Log;

import com.rn5.fitnesstracker.define.EventListener;
import com.rn5.libstrava.authentication.api.StravaAuthentication;
import com.rn5.libstrava.authentication.model.AuthenticationResponse;
import com.rn5.libstrava.authentication.model.AuthenticationType;

import androidx.annotation.Nullable;

import static com.rn5.fitnesstracker.define.Constants.STRAVA_TOKEN;

public class StravaAuthenticationAsync extends AsyncTask<String,Void, AuthenticationResponse> {
    private AuthenticationType type;
    private StravaAuthentication stravaAuth;
    private EventListener eventListener;

    private final static String TAG = StravaAuthenticationAsync.class.getSimpleName();

    public StravaAuthenticationAsync (AuthenticationType type, EventListener eventListener) {
        this.type = type;
        this.eventListener = eventListener;
    }

    @Override
    protected AuthenticationResponse doInBackground(String... strings) {
        stravaAuth = new StravaAuthentication(type);
        return stravaAuth.authorize(strings[0]);
    }

    @Override
    protected void onPostExecute(@Nullable AuthenticationResponse result) {
        if (result != null) {
            Log.d(TAG,"Expires : " + result.getExpiresAt());
            stravaAuth.processResponse(result);
            STRAVA_TOKEN.update();
            if (eventListener != null)
                eventListener.onTokenRefreshed();
        }
    }
}
