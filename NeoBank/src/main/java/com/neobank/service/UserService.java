package com.neobank.service;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.user.ChangePasswordDto;
import com.neobank.dto.user.UserEditDto;
import com.neobank.dto.user.UserResponseDto;
import com.neobank.entity.User;
import com.neobank.enums.Messages;
import com.neobank.mapper.UserMapper;
import com.neobank.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<UserResponseDto> getById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        return new ApiResponse<>(true, userMapper.toResponseDto(user), Messages.SUCCESS.name());
    }

    public ApiResponse<UserResponseDto> edit(Long id, UserEditDto dto) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        userRepository.save(user);
        return new ApiResponse<>(true, userMapper.toResponseDto(user), Messages.UPDATED.name());
    }

    public ApiResponse<Void> changePassword(Long id, ChangePasswordDto dto) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getUserPassword()))
            throw new RuntimeException(Messages.INVALID_CREDENTIALS.name());

        user.setUserPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return new ApiResponse<>(true, Messages.UPDATED.name());
    }
}