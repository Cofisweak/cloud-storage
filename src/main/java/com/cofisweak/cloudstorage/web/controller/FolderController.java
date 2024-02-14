package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.utils.ControllerUtils;
import com.cofisweak.cloudstorage.utils.PathUtils;
import com.cofisweak.cloudstorage.web.dto.CreateFolderDto;
import com.cofisweak.cloudstorage.web.dto.DeleteDto;
import com.cofisweak.cloudstorage.web.dto.UploadDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
@RequiredArgsConstructor
public class FolderController {

    private final FileStorageService fileStorageService;

    @GetMapping()
    public String home(Model model,
                       @RequestParam(required = false, defaultValue = "/") String path,
                       @CookieValue(value = "theme", defaultValue = "light") String theme) {
        model.addAttribute("objects", fileStorageService.getFolderContent(path));
        model.addAttribute("breadcrumbs", ControllerUtils.createBreadcrumbsFromPath(path));
        model.addAttribute("currentPath", path);
        model.addAttribute("rootPath", PathUtils.getRootFolder(path));
        model.addAttribute("theme", theme);
        return "home";
    }

    @PostMapping("/createFolder")
    public String createFolder(@ModelAttribute("createFolderDto") @Validated CreateFolderDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

            if (bindingResult.hasFieldErrors("path")) {
                return "redirect:/";
            } else {
                return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
            }
        }

        try {
            fileStorageService.createFolder(dto);
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@RequestParam String path, HttpServletResponse response, OutputStream outputStream) {
        String filename = PathUtils.extractObjectName(path) + ".zip";
        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
        fileStorageService.downloadFolder(path, outputStream);
    }

    @PostMapping("/delete")
    public String deleteFolder(@ModelAttribute("deleteDto") @Validated DeleteDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

            if (bindingResult.hasFieldErrors("path")) {
                return "redirect:/";
            } else {
                return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
            }
        }

        try {
            fileStorageService.deleteFolder(dto);
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
    }

    @PostMapping("/upload")
    public String upload(@ModelAttribute("uploadDto") @Validated UploadDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

            if (bindingResult.hasFieldErrors("path")) {
                return "redirect:/";
            } else {
                return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
            }
        }

        try {
            fileStorageService.upload(dto);
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
    }
}
