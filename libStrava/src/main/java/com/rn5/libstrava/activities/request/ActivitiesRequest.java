package com.rn5.libstrava.activities.request;

import com.rn5.libstrava.activities.api.ActivityAPI;
import com.rn5.libstrava.activities.model.Activities;
import com.rn5.libstrava.activities.model.Activity;
import com.rn5.libstrava.activities.rest.ActivityRest;

import java.util.List;

import retrofit2.Call;

import static com.rn5.libstrava.common.model.Constants.TOKEN;

public class ActivitiesRequest {

    private final Long after;
    private final Long before;
    private final ActivityRest restService;
    private final ActivityAPI api;

    public ActivitiesRequest(Long after, Long before, ActivityRest restService, ActivityAPI api) {
        this.after = after;
        this.before = before;
        this.restService = restService;
        this.api = api;
    }

    public List<Activity> execute() {
        Call<List<Activity>> call = restService.getActivities(TOKEN.toString(),after,before,1, 100);
        return api.execute(call);
    }
}
