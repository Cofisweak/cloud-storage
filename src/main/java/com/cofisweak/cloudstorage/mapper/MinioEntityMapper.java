package com.cofisweak.cloudstorage.mapper;

import com.cofisweak.cloudstorage.web.dto.StorageEntityDto;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.cofisweak.cloudstorage.utils.PathUtils.extractObjectName;
import static com.cofisweak.cloudstorage.utils.PathUtils.trimUserFolder;

@Component
public class MinioEntityMapper {

    @SneakyThrows
    public List<StorageEntityDto> map(Iterable<Result<Item>> entities, String rootPath, boolean includeSelf) {
        List<StorageEntityDto> result = new ArrayList<>();

        for (Result<Item> entity : entities) {
            Item item = entity.get();

            if (!includeSelf && item.objectName().equals(rootPath)) {
                continue;
            }

            StorageEntityDto dto = map(item);
            result.add(dto);
        }
        return result;
    }

    public StorageEntityDto map(Item item) {
        return StorageEntityDto.builder()
                .objectName(extractObjectName(item.objectName()))
                .path(trimUserFolder(item.objectName()))
                .isDirectory(item.objectName().endsWith("/"))
                .size(stringifySize(item.size()))
                .createdAt(stringifyCreatedAt(item))
                .build();
    }

    private String stringifyCreatedAt(Item item) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
                .withZone(ZoneId.systemDefault());
        try {
            return item.lastModified().format(formatter);
        } catch (NullPointerException ignored) {}
        return null;
    }

    private String stringifySize(long size) {
        String[] prefix = new String[]{"B", "KB", "MB", "GB", "TB"};
        DecimalFormat formatter = new DecimalFormat("#.#");
        int index = 0;
        double resultSize = size;
        while (resultSize >= 1024) {
            resultSize /= 1024;
            index++;
        }
        return formatter.format(resultSize) + prefix[index];
    }
}
