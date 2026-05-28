package com.neobank.controller;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.user.ChangePasswordDto;
import com.neobank.dto.user.UserEditDto;
import com.neobank.dto.user.UserResponseDto;
import com.neobank.service.UserService;
import com.neobank.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponseDto> getMe() {
        return userService.getById(SecurityUtil.getCurrentUserId());
    }

    @PutMapping("/me")
    public ApiResponse<UserResponseDto> edit(@RequestBody UserEditDto dto) {
        return userService.edit(SecurityUtil.getCurrentUserId(), dto);
    }

    @PatchMapping("/me/password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordDto dto) {
        return userService.changePassword(SecurityUtil.getCurrentUserId(), dto);
    }
}