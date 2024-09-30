package com.laojiahuo.ictproject.AO;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "UploadResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    private String minIoUrl;

    private String nginxUrl;

    public UploadResponse(String url) {
        this.minIoUrl = url;
    }

}