package com.product.service;

import java.util.List;

import com.product.dto.AddProductDto;
import com.product.entity.Product;

public interface ProductService {
	
	public String addNewProductServiceString(AddProductDto dto); 
	
	public Product getProductByIdService(Long id);
	
	public Product deleteProductByIdService(Long Id);
	
	public Product updateProductByIdService(Long id, AddProductDto dto);
	
	public List<Product> getProductsByCatagory(String catagory, String sorting);
	
	public List<Product> getProductByPageService(Integer pageNo, Integer pageSize, String sorting);
	public List<Product> getProductByPageService(Integer pageNo, Integer pageSize);
	
	public String incProductStock(Long id, int stockAmount);
	public String decProductStock(Long id, int stockAmount); 

}
