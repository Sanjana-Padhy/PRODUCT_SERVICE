package com.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProfileResponseDto {

    private String name;
    private String email;
    private String role;
}