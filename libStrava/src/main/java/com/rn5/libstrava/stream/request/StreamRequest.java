package com.rn5.libstrava.stream.request;

import com.rn5.libstrava.stream.api.StreamAPI;
import com.rn5.libstrava.stream.model.Stream;
import com.rn5.libstrava.stream.rest.StreamRest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.rn5.libstrava.common.model.Constants.TOKEN;

public class StreamRequest {
    private final Long id;
    private final String keys = "time,heartrate,watts,moving";
    private final StreamRest restService;
    private final StreamAPI api;

    public StreamRequest(Long id, StreamRest restService, StreamAPI api) {
        this.id = id;
        this.restService = restService;
        this.api = api;
    }

    public Stream execute() {
        Call<Stream> call = restService.getStreams(TOKEN.toString(),id,keys,true);
        return api.execute(call);
    }
}
