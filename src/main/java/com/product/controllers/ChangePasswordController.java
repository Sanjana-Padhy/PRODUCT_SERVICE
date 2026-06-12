package com.product.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.product.dto.ChangePasswordDto;
import com.product.service.UserService;

@RestController
@RequestMapping("/api/v1.0/password")
public class ChangePasswordController {

    @Autowired
    private UserService userService;

    @PutMapping
    public String changePassword(
            @RequestBody ChangePasswordDto dto,
            Authentication authentication) {

        return userService.changePassword(
                authentication.getName(),
                dto);
    }
}