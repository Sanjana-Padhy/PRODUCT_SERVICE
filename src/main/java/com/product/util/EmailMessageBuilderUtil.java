package com.product.util;

import org.springframework.stereotype.Component;

@Component
public class EmailMessageBuilderUtil {

	public String otpMessageBuilder(String name, Integer otp) {
		StringBuilder message=new StringBuilder();
		message.append("Dear "+name+",\n");
		message.append("Thank you for your interest in Delivery Service.");
		message.append("For your new account registration the 6 digit otp is here :"+otp);
		message.append("\nNote: OTP is valid only for one minute");
		return message.toString();
	}
	
	public String userRegisterMessageBuilder(String name) {
		StringBuilder message=new StringBuilder();
		message.append("Dear "+name+",\n");
		message.append("Thanks for your interst for our service, Delivery Service.\n ");
		message.append("You have been register as user.\n");
		message.append("Note: This is a system generated email.Do not reply.");
		return message.toString();
		
				
	}
}
