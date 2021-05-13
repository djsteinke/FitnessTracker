package com.rn5.libstrava.gear.request;

import retrofit2.Call;
import com.rn5.libstrava.gear.api.GearAPI;
import com.rn5.libstrava.gear.model.Gear;
import com.rn5.libstrava.gear.rest.GearRest;

public class GearRequest {

    private final String gearId;
    private final GearRest restService;
    private final GearAPI api;

    public GearRequest(String gearId, GearRest restService, GearAPI api) {
        this.gearId = gearId;
        this.restService = restService;
        this.api = api;
    }

    public Gear execute() {
        Call<Gear> call = restService.getGear(gearId);
        return api.execute(call);
    }
}
