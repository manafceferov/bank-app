package com.neobank.service;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.auth.LoginRequestDto;
import com.neobank.dto.auth.LoginResponseDto;
import com.neobank.dto.auth.RegisterRequestDto;
import com.neobank.entity.User;
import com.neobank.enums.Messages;
import com.neobank.mapper.UserMapper;
import com.neobank.repository.UserRepository;
import com.neobank.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ApiResponse<Void> register(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException(Messages.ALREADY_EXISTS.name());
        if (dto.getFinCode() != null && userRepository.existsByFinCode(dto.getFinCode()))
            throw new RuntimeException(Messages.ALREADY_EXISTS.name());

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUserPassword(passwordEncoder.encode(dto.getPassword())); // ← düzəliş
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setFinCode(dto.getFinCode());
        userRepository.save(user);

        return new ApiResponse<>(true, Messages.CREATED.name());
    }

    public ApiResponse<LoginResponseDto> login(LoginRequestDto dto) {
        User user = userRepository.findByEmailAndDeletedFalse(dto.getEmail())
                .orElseThrow(() -> new RuntimeException(Messages.INVALID_CREDENTIALS.name()));

        if (!passwordEncoder.matches(dto.getPassword(), user.getUserPassword())) // ← düzəliş
            throw new RuntimeException(Messages.INVALID_CREDENTIALS.name());

        if (!user.getActive())
            throw new RuntimeException(Messages.FORBIDDEN.name());

        LoginResponseDto response = userMapper.toLoginResponse(user);
        response.setToken(jwtUtil.generateToken(user.getEmail(), user.getRole().name()));

        return new ApiResponse<>(true, response, Messages.SUCCESS.name());
    }
}