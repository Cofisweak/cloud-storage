package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.utils.PathUtils;
import com.cofisweak.cloudstorage.web.dto.Breadcrumb;
import com.cofisweak.cloudstorage.web.dto.CreateFolderDto;
import com.cofisweak.cloudstorage.web.dto.DeleteDto;
import com.cofisweak.cloudstorage.web.dto.UploadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.cofisweak.cloudstorage.utils.Utils.mapValidationResultToErrorMessage;

@Controller
@RequiredArgsConstructor
public class FolderController {

    private final FileStorageService fileStorageService;

    @GetMapping()
    public String home(Model model,
                       @RequestParam(required = false, defaultValue = "/") String path,
                       @CookieValue(value = "theme", defaultValue = "light") String theme) {
        model.addAttribute("objects", fileStorageService.getFolderContent(path));
        model.addAttribute("breadcrumbs", createBreadcrumbsFromPath(path));
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
            String errorMessage = mapValidationResultToErrorMessage(bindingResult);
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

    @PostMapping("/delete")
    public String deleteFolder(@ModelAttribute("deleteDto") @Validated DeleteDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = mapValidationResultToErrorMessage(bindingResult);
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
            String errorMessage = mapValidationResultToErrorMessage(bindingResult);
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

    private List<Breadcrumb> createBreadcrumbsFromPath(String path) {
        List<Breadcrumb> result = new ArrayList<>();

        result.add(new Breadcrumb("Home", "/"));

        String[] objects = path.split("/");
        String currentPath = "/";
        for (String object : objects) {
            if(object.isBlank()) continue;
            currentPath += object + "/";
            result.add(new Breadcrumb(object, currentPath));
        }

        if(!path.endsWith("/") && result.size() > 1) {
            result.remove(result.size() - 1);
        }
        return result;
    }
}
