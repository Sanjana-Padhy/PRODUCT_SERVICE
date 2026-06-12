package com.product.service;

import com.product.dto.AddUserDto;
import com.product.dto.ChangePasswordDto;
import com.product.dto.EmailOtpVerifyDto;
import com.product.dto.ForgotPasswordDto;
import com.product.dto.ResetPasswordDto;
import com.product.dto.VerifyResetOtpDto;

public interface UserService {
	
	public String initiateUserVerificationService(AddUserDto dto);
	
	public String finalUserVerificationService(EmailOtpVerifyDto dto);
	
	public String changePassword(
	        String email,
	        ChangePasswordDto dto);
	
	public String forgotPasswordService(ForgotPasswordDto dto);

	public String verifyResetOtpService(VerifyResetOtpDto dto);

	public String resetPasswordService(ResetPasswordDto dto);
}
