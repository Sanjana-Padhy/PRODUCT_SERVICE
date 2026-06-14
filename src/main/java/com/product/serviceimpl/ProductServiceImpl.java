package com.product.serviceimpl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.product.dto.AddProductDto;
import com.product.entity.Product;
import com.product.mapping.ModelMapper;
import com.product.repository.ProductRepository;
import com.product.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.product.dto.DashboardDto;
import com.product.repository.UserRepository;

@Service
@RequiredArgsConstructor 
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepo;
	
	private final UserRepository userRepo;
	
	private final ModelMapper modelmapper;

	@Override
	@Transactional
	public String addNewProductServiceString(AddProductDto dto) {
		Product product=modelmapper.getProductFromAddProductDtoMapper(dto);
		product=productRepo.save(product);
		String response="product saved inside inventry with product id"+product.getPid();
		return response; 
	}

	@Override
	// @Cacheable(value = "product",key="#id")
	public Product getProductByIdService(Long id) {
		Optional<Product> optProduct=productRepo.findById(id);
		if(optProduct.isEmpty()) {
			throw new NoSuchElementException("No product found with id" +id);
		}
		Product product=optProduct.get();
		if(!product.getIsActive() ) {
			throw new NoSuchElementException("No product found with id" +id);
		}
		return product;
	}

	@Override
	@Transactional
	public Product deleteProductByIdService(Long Id) {
		Product product=getProductByIdService(Id);
		product.setIsActive(false);
		product=productRepo.save(product);
		return product;
	}

	@Override
	@Transactional 
	public Product updateProductByIdService(Long id, AddProductDto dto) {
		Product product=getProductByIdService(id);
		product.setName(dto.getName());		
		product.setBrand(dto.getBrand());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setCatagory(dto.getCatagory());
		product.setImageUrl(dto.getImageUrl());
		product.setStock(dto.getStock());
		product.setUpdateAt(LocalDateTime.now());
		product=productRepo.save(product);
		return product;
	}

	@Override
	public List<Product> getProductsByCatagory(String catagory, String sorting) {
		catagory=catagory.toUpperCase();
		sorting=sorting.toUpperCase();
		Comparator<Product> asc=(i,j)->i.getPrice().compareTo(j.getPrice());
		Comparator<Product> desc=(i,j)->j.getPrice().compareTo(i.getPrice());
		Comparator<Product> comparator=(sorting.equals("ASC")) ? asc:desc;
		List<Product> products=productRepo.getByCatagory(catagory);
		products=products.stream().filter(i->i.getIsActive()).sorted(comparator).toList();
		return 	products;
	}
	
	@Override
	public List<Product> getProductsByCategoryPage(
	        String category,
	        Integer pageNo,
	        Integer pageSize
	) {

	    category = category.toUpperCase();

	    Pageable pageable =
	        PageRequest.of(
	            pageNo,
	            pageSize,
	            Sort.by("pid").descending()
	        );

	    Page<Product> page =
	        productRepo.findByCatagoryAndIsActiveTrue(
	            category,
	            pageable
	        );

	    return page.getContent();
	}

	@Override
	public List<Product> getProductByPageService(Integer pageNo, Integer pageSize, String sorting) {
		if(sorting==null || sorting.equalsIgnoreCase("NONE")) {
			return getProductByPageService(pageNo, pageSize); 
		}
		Sort sort=(sorting.equalsIgnoreCase("ASC"))?
				Sort.by("price").ascending():Sort.by("price").descending();
		Pageable pageable=PageRequest.of(pageNo, pageSize, sort);
		Page<Product> page=productRepo.findAll(pageable);
		return page.getContent();
	}

	@Override
	public List<Product> getProductByPageService(
	        Integer pageNo,
	        Integer pageSize) {

	    Pageable pageable =
	            PageRequest.of(
	                    pageNo,
	                    pageSize,
	                    Sort.by("pid").descending()
	            );

	    Page<Product> page =
	            productRepo.findByIsActiveTrue(pageable);

	    return page.getContent();
	}
 
	@Override
	@Transactional
	public String incProductStock(Long id, int stockAmount) {
		Product product=getProductByIdService(id);
		product.setStock(product.getStock()+stockAmount);
		product= productRepo.save(product);
		return "new inventory stock is "+product.getStock();
	}

	@Override
	@Transactional
	public String decProductStock(Long id, int stockAmount) {
		Product product=getProductByIdService(id);
		if(product.getStock()<stockAmount) {
			throw new RuntimeException("Insufficient stocks");
		}
		product.setStock(product.getStock()-stockAmount);
		product= productRepo.save(product);
		return "new inventory stock is "+product.getStock(); 
	}
	
	
	@Override
	public List<Product> searchProductByName(String name) {

	    return productRepo
	            .findByNameContainingIgnoreCaseAndIsActiveTrue(name);
	}

	@Override
	public DashboardDto getDashboardData() {

	    long totalProducts =
	            productRepo.countByIsActiveTrue();

	    long totalUsers =
	            userRepo.count();

	    long inStockProducts =
	            productRepo.countByStockGreaterThanAndIsActiveTrue(0);

	    long outOfStockProducts =
	            productRepo.countByStockEqualsAndIsActiveTrue(0);

	    return DashboardDto.builder()
	            .totalProducts(totalProducts)
	            .totalUsers(totalUsers)
	            .inStockProducts(inStockProducts)
	            .outOfStockProducts(outOfStockProducts)
	            .build();
	}
}
