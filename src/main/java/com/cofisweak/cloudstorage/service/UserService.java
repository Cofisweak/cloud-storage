package com.cofisweak.cloudstorage.service;

import com.cofisweak.cloudstorage.domain.User;
import com.cofisweak.cloudstorage.web.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    void register(RegisterDto dto);
}
