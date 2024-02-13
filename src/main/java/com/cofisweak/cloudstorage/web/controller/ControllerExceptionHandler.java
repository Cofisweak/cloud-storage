package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler({NoResourceFoundException.class, ObjectNotFoundException.class})
    public String handleNotFound() {
        return "not-found";
    }

    @ExceptionHandler(Throwable.class)
    public String handleException(Throwable e, RedirectAttributes redirectAttributes) {

        //@ExceptionHandler(MaxUploadSizeExceededException.class) doesn't handle exception correctly
        if (e instanceof MaxUploadSizeExceededException) {
            redirectAttributes.addFlashAttribute("errorMessage", "Files were not uploaded. One or more files exceed size limits (20MB per file, 500MB per upload request)");
            return "redirect:/";
        }

        log.error("An error occurred while processing request: ", e);
        redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong... Try later");
        return "redirect:/";
    }
}
