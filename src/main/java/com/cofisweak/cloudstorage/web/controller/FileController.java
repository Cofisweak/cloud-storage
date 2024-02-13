package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.FileStorageException;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.web.dto.DeleteDto;
import com.cofisweak.cloudstorage.web.dto.DownloadDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.cofisweak.cloudstorage.utils.Utils.mapValidationResultToErrorMessage;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody Resource getFile(@RequestParam String path, HttpServletResponse response) throws IOException {
        DownloadDto file = fileStorageService.downloadFile(path);
        byte[] bytes = file.getStream().readAllBytes();
        String encodedFileName = URLEncoder.encode(file.getFilename(), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
        return new ByteArrayResource(bytes);
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
            fileStorageService.deleteFile(dto);
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
    }
}
