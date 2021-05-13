package com.rn5.libstrava.upload.api;

import java.io.File;

import com.rn5.libstrava.common.api.StravaAPI;
import com.rn5.libstrava.common.api.StravaConfig;
import com.rn5.libstrava.upload.request.CheckUploadStatusRequest;
import com.rn5.libstrava.upload.request.UploadFileRequest;
import com.rn5.libstrava.upload.rest.UploadRest;

public class UploadAPI extends StravaAPI {

    public UploadAPI(StravaConfig config) {
        super(config);
    }

    public UploadFileRequest uploadFile(File file) {
        return new UploadFileRequest(file, getAPI(UploadRest.class), this);
    }

    public CheckUploadStatusRequest checkUploadStatus(long id) {
        return new CheckUploadStatusRequest(id, getAPI(UploadRest.class), this);
    }
}
