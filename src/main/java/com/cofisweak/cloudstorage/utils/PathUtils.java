package com.cofisweak.cloudstorage.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@UtilityClass
public class PathUtils {
    public static String extractObjectName(String path) {
        List<String> folders = Arrays.stream(path.split("/"))
                .filter(folder -> !folder.isBlank())
                .toList();
        return folders.get(folders.size() - 1);
    }

    public static String getRootFolder(String path) {
        StringJoiner joiner = new StringJoiner("/", "/", "");
        String[] split = path.split("/");
        for (int i = 0; i < split.length - 1; i++) {
            String folder = split[i];
            if (folder.isBlank()) continue;
            joiner.add(folder);
        }
        String rootFolderPath = joiner.toString();
        if (rootFolderPath.equals("/")) {
            return rootFolderPath;
        } else {
            return rootFolderPath + "/";
        }
    }
}
