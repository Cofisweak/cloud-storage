package com.cofisweak.cloudstorage.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @NotBlank(message = "Should be specified")
    @Length(min = 3, max = 32, message = "Length should be more than 3 letters and less than 32 letters")
    @Pattern(regexp = "^[A-Za-z0-9@._\\-+]+$", message = "Contains illegal characters")
    private String username;

    @NotBlank(message = "Should be specified")
    @Length(min = 8, max = 32, message = "Length should be more than 8 symbols and less than 32 symbols")
    private String password;

    @NotBlank(message = "Should be specified")
    @Length(min = 8, max = 32, message = "Length should be more than 8 symbols and less than 32 symbols")
    private String passwordConfirmation;
}
