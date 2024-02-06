package com.cofisweak.cloudstorage.web.controller;

import com.cofisweak.cloudstorage.domain.exception.PasswordsNotEqualsException;
import com.cofisweak.cloudstorage.domain.exception.UsernameAlreadyExistsException;
import com.cofisweak.cloudstorage.service.UserService;
import com.cofisweak.cloudstorage.web.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("user") RegisterDto dto) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Validated RegisterDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(dto);
        } catch (PasswordsNotEqualsException e) {
            bindingResult.rejectValue("passwordConfirmation","", e.getMessage());
            return "auth/register";
        } catch (UsernameAlreadyExistsException e) {
            bindingResult.rejectValue("username", "", e.getMessage());
            return "auth/register";
        }

        return "redirect:/auth/login";
    }
}
