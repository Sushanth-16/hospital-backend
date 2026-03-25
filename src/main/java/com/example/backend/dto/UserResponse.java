package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.example.backend.model.UserRole;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String username;
    private UserRole role;
    private Long referenceId;
}
