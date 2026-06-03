package com.product.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddUserDto {
	@NotBlank(message = "User name can't be empty")
	private String name;
	@Email(message = "Please enter valid email")
	private String email;
	@NotBlank(message = "Password can't be empty")
	private String password;
	@NotBlank(message = "Role can't be blank")
	private String role;
	
}
