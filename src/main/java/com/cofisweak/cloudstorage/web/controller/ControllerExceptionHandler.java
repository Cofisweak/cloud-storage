package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler({NoResourceFoundException.class, ObjectNotFoundException.class})
    public String handleNotFound(Model model) {
        model.addAttribute("message", "Resource not found");
        return "error-page";
    }

    @ExceptionHandler(Throwable.class)
    public String handleException(Throwable e, RedirectAttributes redirectAttributes, Model model) {

        //@ExceptionHandler(MaxUploadSizeExceededException.class) doesn't handle exception correctly
        if (e instanceof MaxUploadSizeExceededException) {
            redirectAttributes.addFlashAttribute("errorMessage", "Files were not uploaded. One or more files exceed size limits (20MB per file, 500MB per upload request)");
            return "redirect:/";
        }

        log.error("An error occurred while processing request: ", e);
        model.addAttribute("message", "Something went wrong... Try later");
        return "error-page";
    }
}
