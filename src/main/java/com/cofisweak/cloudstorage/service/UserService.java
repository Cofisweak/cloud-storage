package com.cofisweak.cloudstorage.service;

import com.cofisweak.cloudstorage.web.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void register(RegisterDto dto);
}
