package com.rn5.libstrava.gear.api;

import com.rn5.libstrava.common.api.StravaAPI;
import com.rn5.libstrava.common.api.StravaConfig;
import com.rn5.libstrava.gear.request.GearRequest;
import com.rn5.libstrava.gear.rest.GearRest;

public class GearAPI extends StravaAPI {

    public GearAPI(StravaConfig config) {
        super(config);
    }

    public GearRequest getGear(String gearId) {
        return new GearRequest(gearId, getAPI(GearRest.class), this);
    }
}
