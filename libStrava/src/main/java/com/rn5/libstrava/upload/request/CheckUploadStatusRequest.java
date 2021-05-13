package com.rn5.libstrava.upload.request;

import retrofit2.Call;
import com.rn5.libstrava.upload.api.UploadAPI;
import com.rn5.libstrava.upload.model.UploadStatus;
import com.rn5.libstrava.upload.rest.UploadRest;

public class CheckUploadStatusRequest {

    private final long id;
    private final UploadRest uploadRest;
    private final UploadAPI uploadAPI;

    public CheckUploadStatusRequest(long id, UploadRest uploadRest, UploadAPI uploadAPI) {
        this.id = id;
        this.uploadRest = uploadRest;
        this.uploadAPI = uploadAPI;
    }

    public UploadStatus execute() {
        Call<UploadStatus> call = uploadRest.checkUploadStatus(id);
        return uploadAPI.execute(call);
    }
}
