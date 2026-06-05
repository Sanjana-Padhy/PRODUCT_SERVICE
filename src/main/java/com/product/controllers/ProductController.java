package com.product.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.product.dto.AddProductDto;
import com.product.dto.ApiResponse;
import com.product.dto.ProductResponseDto;
import com.product.entity.Product;
import com.product.mapping.ModelMapper;
import com.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/product")
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;
	
	private final ModelMapper modelMapper;
	
	@PostMapping
	public ResponseEntity<ApiResponse> addNewProductController(@Valid @RequestBody AddProductDto dto) {
		String serviceResponse=productService.addNewProductServiceString(dto);
		ApiResponse apiresponse=ApiResponse.builder().serviceName("PRODUCT_SERVICE")
				.status(true)
				.type("String").payload(serviceResponse).build();
		return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.OK); 
	}
	
	@GetMapping("/{id}")
	public  ResponseEntity<ApiResponse> getProductByIdController(@PathVariable("id") Long id) {
		Product product=productService.getProductByIdService(id);
		ProductResponseDto dto=modelMapper.entityToProductResponseDtoMapper(product);
		ApiResponse apiresponse=ApiResponse.builder()
				.serviceName("PRODUCT_SERVICE")
				.status(true)
				.type("object")
				.payload(dto)
				.build();
		return new ResponseEntity<ApiResponse>(apiresponse,HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> deleteProductByIdController(
	        @PathVariable("id") Long id) {

	    productService.deleteProductByIdService(id);

	    ApiResponse apiResponse = ApiResponse.builder()
	            .serviceName("PRODUCT_SERVICE")
	            .status(true)
	            .type("string")
	            .payload("Product deleted successfully")
	            .build();

	    return ResponseEntity.ok(apiResponse);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse> updateProductByIdController(@PathVariable("id") Long id, @RequestBody AddProductDto dto) {
		Product product=productService.updateProductByIdService(id, dto);
		ProductResponseDto response=modelMapper.entityToProductResponseDtoMapper(product);
		ApiResponse apiresponse=ApiResponse.builder()
				.serviceName("PRODUCT_SERVICE")
				.status(true)
				.type("object")
				.payload(response).build();
		return ResponseEntity.ok(apiresponse);
	}
	
	@GetMapping("/catagory")
	public ResponseEntity<?> getByCategory(
	        @RequestParam String catagory,
	        @RequestParam(defaultValue = "0") Integer pageNo,
	        @RequestParam(defaultValue = "5") Integer pageSize
	){

	    List<Product> products =
	            productService.getProductsByCategoryPage(
	                    catagory,
	                    pageNo,
	                    pageSize
	            );

	    return ResponseEntity.ok(products);
	}
	
	@GetMapping("/page")
	public ResponseEntity<ApiResponse> getProductByPage(@RequestParam(name ="pageNo", required = false, defaultValue = "0") Integer pageNo, @RequestParam(name = "pageSize",
			required = false,
			defaultValue = "100")
			Integer pageSize, @RequestParam(name = "sorting", required = false, defaultValue = "NONE") String sorting) {
		List<Product> products =productService.getProductByPageService(pageNo, pageSize, sorting);
		List<ProductResponseDto> dtos= products.stream()
				.map(p->modelMapper.entityToProductResponseDtoMapper(p)).toList(); 
		ApiResponse apiResponse=ApiResponse.builder()
				.serviceName("PRODUCT_SERVICE")
				.status(true)
				.type("object array")
				.payload(dtos).build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PatchMapping("/stock/inc/{id}/{amount}")
	public ResponseEntity<ApiResponse> incStocksController(@PathVariable("id") Long id,@PathVariable("amount") Integer stockAmount) {
		String serviceResponse=productService.incProductStock(id, stockAmount);
		ApiResponse apiResponse=new ApiResponse("PRODUCT_SERVICE",true,"string",serviceResponse);
		return ResponseEntity.ok(apiResponse);
	}
	
	@PatchMapping("/stock/dec/{id}/{amount}")
	public ResponseEntity<ApiResponse> decStocksController(@PathVariable("id") Long id,@PathVariable("amount") Integer stockAmount) {
		String serviceResponse=productService.decProductStock(id, stockAmount);
		ApiResponse apiResponse=new ApiResponse("PRODUCT_SERVICE",true,"string",serviceResponse);
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.OK);
	}
	
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse> searchProducts(
	        @RequestParam String keyword){

	    List<Product> products =
	            productService.searchProductByName(keyword);

	    List<ProductResponseDto> dtos =
	            products.stream()
	            .map(modelMapper::entityToProductResponseDtoMapper)
	            .toList();

	    ApiResponse response = ApiResponse.builder()
	            .serviceName("PRODUCT_SERVICE")
	            .status(true)
	            .type("object array")
	            .payload(dtos)
	            .build();

	    return ResponseEntity.ok(response);
	}
} 
