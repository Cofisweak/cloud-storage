package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.ObjectNotFoundException;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.utils.ControllerUtils;
import com.cofisweak.cloudstorage.web.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final FileStorageService fileStorageService;

    @GetMapping
    public String search(@Validated @ModelAttribute("searchDto") SearchDto dto, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes, Model model,
                         @CookieValue(value = "theme", defaultValue = "light") String theme) {
        if (bindingResult.hasErrors()) {
            String errorMessage = ControllerUtils.mapValidationResultToErrorMessage(bindingResult);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
        }

        try {
            model.addAttribute("theme", theme);
            model.addAttribute("foundObjects", fileStorageService.search(dto.getPath(), dto.getQuery()));
        } catch (ObjectNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/?path=" + URLEncoder.encode(dto.getPath(), StandardCharsets.UTF_8);
        }

        return "search";
    }
}
