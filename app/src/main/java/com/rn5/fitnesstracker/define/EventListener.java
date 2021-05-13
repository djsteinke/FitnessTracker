package com.rn5.fitnesstracker.define;

public interface EventListener {
    void onActivitySynced();
    void onTokenRefreshed();
    void onToast(String msg);
}
