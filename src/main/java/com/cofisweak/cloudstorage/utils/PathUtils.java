package com.cofisweak.cloudstorage.utils;

import com.cofisweak.cloudstorage.domain.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@UtilityClass
public class PathUtils {
    public static String trimUserFolder(String path) {
        int index = path.indexOf("/");
        return path.substring(index);
    }

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

    public static String resolveToStoragePath(String path) {
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        String userRootFolderName = getUserRootFolderName(userId);
        return userRootFolderName + path;
    }

    public static String getUserRootFolderName(Long id) {
        String pattern = "user-%d-files";
        return pattern.formatted(id);
    }

    public static String composePath(String... paths) {
        String resultPath = Arrays.stream(paths)
                .map(path -> path.split("/"))
                .flatMap(Arrays::stream)
                .filter(path -> !path.isBlank())
                .collect(Collectors.joining("/", "/", ""));

        if(paths[paths.length - 1].endsWith("/")) {
            resultPath += "/";
        }

        return resultPath;
    }
}
