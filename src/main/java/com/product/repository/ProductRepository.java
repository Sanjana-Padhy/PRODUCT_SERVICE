
package com.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	
	List<Product> getByCatagory(String catagory); 
	
	
	List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

	Page<Product> findByIsActiveTrue(Pageable pageable);
	
	Page<Product> findByCatagoryAndIsActiveTrue(
	        String catagory,
	        Pageable pageable
	);
	
}
