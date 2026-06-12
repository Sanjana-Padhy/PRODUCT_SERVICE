package com.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyResetOtpDto {

    private String email;

    private String otp;

}