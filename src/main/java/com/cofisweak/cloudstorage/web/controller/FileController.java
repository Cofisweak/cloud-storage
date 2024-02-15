package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.utils.ControllerUtils;
import com.cofisweak.cloudstorage.utils.PathUtils;
import com.cofisweak.cloudstorage.web.dto.DeleteDto;
import com.cofisweak.cloudstorage.web.dto.DownloadFileDto;
import com.cofisweak.cloudstorage.web.dto.RenameDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping()
    public String home(Model model,
                       @RequestParam(required = false, defaultValue = "/") String path,
                       @CookieValue(value = "theme", defaultValue = "light") String theme) {
        model.addAttribute("file", fileStorageService.getFile(path));
        model.addAttribute("breadcrumbs", ControllerUtils.createBreadcrumbsFromPath(path));
        model.addAttribute("rootPath", PathUtils.getRootFolder(path));
        model.addAttribute("theme", theme);
        return "file";
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody Resource downloadFile(@RequestParam String path, HttpServletResponse response) throws IOException {
        DownloadFileDto file = fileStorageService.downloadFile(path);
        byte[] bytes = file.getStream().readAllBytes();
        String encodedFileName = URLEncoder.encode(file.getFilename(), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
        return new ByteArrayResource(bytes);
    }

    @PostMapping("/delete")
    public String deleteFile(@ModelAttribute("deleteDto") @Validated DeleteDto dto,
                             BindingResult bindingResult, @RequestParam("path") String path,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
        }

        try {
            fileStorageService.deleteFile(path, dto.getObjectName());
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @PostMapping("/rename")
    public String renameFile(@ModelAttribute("renameDto") @Validated RenameDto dto,
                             BindingResult bindingResult, @RequestParam("path") String path,
                             @RequestParam(value = "isFilePage", required = false, defaultValue = "false") boolean isFilePage,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            if (isFilePage && !bindingResult.hasFieldErrors("oldObjectName")) {
                return "redirect:/file?path=" + URLEncoder.encode(path + dto.getOldObjectName(), StandardCharsets.UTF_8);
            } else {
                return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
            }

        }

        try {
            fileStorageService.renameFile(path, dto.getOldObjectName(), dto.getNewObjectName());
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        if (isFilePage) {
            return "redirect:/file?path=" + URLEncoder.encode(path + dto.getNewObjectName(), StandardCharsets.UTF_8);
        } else {
            return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
        }

    }
}
