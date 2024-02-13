package com.cofisweak.cloudstorage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;

@Data
@AllArgsConstructor
public class UploadFile {
    private String path;
    private InputStream data;
}
