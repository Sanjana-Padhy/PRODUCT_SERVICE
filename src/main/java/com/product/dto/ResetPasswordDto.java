package com.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {

    private String email;

    private String newPassword;

}