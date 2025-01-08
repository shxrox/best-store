package com.boostmytol.beststore.service;

import com.boostmytol.beststore.models.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Products, Integer> {

}
