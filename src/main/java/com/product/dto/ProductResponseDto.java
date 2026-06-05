package com.product.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
	
	private Long pid;
	
	private String name;
	private String description;
	
	private String imageUrl;
	
	private String brand;
	private String catagory;
	private BigDecimal price;

}
