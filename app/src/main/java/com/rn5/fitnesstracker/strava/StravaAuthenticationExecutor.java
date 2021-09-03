package com.rn5.fitnesstracker.strava;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.rn5.fitnesstracker.util.EventListener;
import com.rn5.libstrava.authentication.api.StravaAuthentication;
import com.rn5.libstrava.authentication.model.AuthenticationResponse;
import com.rn5.libstrava.authentication.model.AuthenticationType;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.rn5.fitnesstracker.util.Constants.STRAVA_TOKEN;

public class StravaAuthenticationExecutor {

    private final static String TAG = StravaAuthenticationExecutor.class.getSimpleName();

    private final AuthenticationType type;
    private final EventListener eventListener;
    private final String auth;

    public StravaAuthenticationExecutor(@NonNull AuthenticationType type, @Nullable EventListener eventListener, @NonNull String auth) {
        this.type = type;
        this.eventListener = eventListener;
        this.auth = auth;
    }

    public void run() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(getRunnable(auth));
    }

    private Runnable getRunnable(String val) {
        return () -> {
            Handler handler = new Handler(Looper.getMainLooper());
            final StravaAuthentication stravaAuth = new StravaAuthentication(type);
            AuthenticationResponse response = stravaAuth.authorize(val);
            handler.post(() -> {
                Log.d(TAG,"Expires : " + response.getExpiresAt());
                stravaAuth.processResponse(response);
                STRAVA_TOKEN.update();
                if (eventListener != null)
                    eventListener.onTokenRefreshed();
            });
        };
    }
}
