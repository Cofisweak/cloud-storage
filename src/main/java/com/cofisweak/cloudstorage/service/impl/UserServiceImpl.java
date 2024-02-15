package com.cofisweak.cloudstorage.service.impl;

import com.cofisweak.cloudstorage.domain.User;
import com.cofisweak.cloudstorage.domain.exception.PasswordsNotEqualsException;
import com.cofisweak.cloudstorage.domain.exception.UsernameAlreadyExistsException;
import com.cofisweak.cloudstorage.mapper.UserMapper;
import com.cofisweak.cloudstorage.repository.UserRepository;
import com.cofisweak.cloudstorage.service.FileStorageService;
import com.cofisweak.cloudstorage.service.UserService;
import com.cofisweak.cloudstorage.web.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegisterDto dto) {
        if (!dto.getPassword().equals(dto.getPasswordConfirmation())) {
            throw new PasswordsNotEqualsException("Password should be equals");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("User with this username already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("Created new user: {}", dto.getUsername());
        fileStorageService.createUserDirectory(user.getId());
        log.info("Created user directory for user: {}", dto.getUsername());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
