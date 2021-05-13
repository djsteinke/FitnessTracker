package com.rn5.libstrava.stream.api;

import com.rn5.libstrava.common.api.StravaAPI;
import com.rn5.libstrava.common.api.StravaConfig;
import com.rn5.libstrava.stream.request.StreamRequest;
import com.rn5.libstrava.stream.rest.StreamRest;

public class StreamAPI extends StravaAPI {

    public StreamAPI(StravaConfig config) {
        super(config);
    }

    public StreamRequest getStreams(Long id) {
        return new StreamRequest(id, getAPI(StreamRest.class), this);
    }
}
