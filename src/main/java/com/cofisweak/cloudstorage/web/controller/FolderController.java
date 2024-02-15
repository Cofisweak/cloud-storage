package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.utils.ControllerUtils;
import com.cofisweak.cloudstorage.utils.PathUtils;
import com.cofisweak.cloudstorage.web.dto.CreateFolderDto;
import com.cofisweak.cloudstorage.web.dto.DeleteDto;
import com.cofisweak.cloudstorage.web.dto.RenameDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

    private final FileStorageService fileStorageService;

    @PostMapping("/create")
    public String createFolder(@ModelAttribute("createFolderDto") @Validated CreateFolderDto dto,
                               BindingResult bindingResult, @RequestParam("path") String path,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
        }

        try {
            fileStorageService.createFolder(path, dto.getFolderName());
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFolder(@RequestParam String path, HttpServletResponse response, OutputStream outputStream) {
        String filename = PathUtils.extractObjectName(path) + ".zip";
        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
        fileStorageService.downloadFolder(path, outputStream);
    }

    @PostMapping("/delete")
    public String deleteFolder(@ModelAttribute("deleteDto") @Validated DeleteDto dto,
                               BindingResult bindingResult, @RequestParam("path") String path,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
        }

        try {
            fileStorageService.deleteFolder(path, dto.getObjectName());
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @PostMapping("/rename")
    public String renameFolder(@ModelAttribute("renameDto") @Validated RenameDto dto,
                             BindingResult bindingResult, @RequestParam("path") String path,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
        }

        try {
            fileStorageService.renameFolder(path, dto.getOldObjectName(), dto.getNewObjectName());
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
