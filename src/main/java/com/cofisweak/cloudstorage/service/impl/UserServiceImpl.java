package com.cofisweak.cloudstorage.service.impl;

import com.cofisweak.cloudstorage.domain.User;
import com.cofisweak.cloudstorage.domain.exception.PasswordsNotEqualsException;
import com.cofisweak.cloudstorage.domain.exception.UsernameAlreadyExistsException;
import com.cofisweak.cloudstorage.mapper.UserMapper;
import com.cofisweak.cloudstorage.repository.UserRepository;
import com.cofisweak.cloudstorage.service.UserService;
import com.cofisweak.cloudstorage.web.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

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
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
