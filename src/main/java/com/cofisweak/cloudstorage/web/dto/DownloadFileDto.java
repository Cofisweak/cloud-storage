package com.cofisweak.cloudstorage.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileDto {
    private String filename;
    private InputStream stream;
}
