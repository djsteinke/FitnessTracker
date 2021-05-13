package com.rn5.libstrava.activities.api;

import com.rn5.libstrava.activities.request.ActivitiesRequest;
import com.rn5.libstrava.activities.rest.ActivityRest;
import com.rn5.libstrava.common.api.StravaAPI;
import com.rn5.libstrava.common.api.StravaConfig;

public class ActivityAPI extends StravaAPI {

    public ActivityAPI(StravaConfig config) {
        super(config);
    }

    public ActivitiesRequest getActivities(Long after, Long before) {
        return new ActivitiesRequest(after, before, getAPI(ActivityRest.class), this);
    }
}
