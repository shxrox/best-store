package com.boostmytol.beststore.controllers;

import com.boostmytol.beststore.models.ProductDto;
import com.boostmytol.beststore.models.Products;
import com.boostmytol.beststore.service.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsRepository repo;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Products> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage (Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/createProduct";
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {
        if (productDto.getImageFileName() == null || productDto.getImageFileName().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFileName", "Image file is required"));
        }

        if (result.hasErrors()) {
            return "products/createProduct";
        }
        //saving images
        MultipartFile image = productDto.getImageFileName();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_"+ image.getOriginalFilename();

        try{
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            try(InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream,Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception ex){
            System.out.println(" Exception "+ ex.getMessage());
        }

        Products products = new Products();
        products.setName(productDto.getName());
        products.setBrand(productDto.getBrand());
        products.setCategory(productDto.getCategory());
        products.setPrice(productDto.getPrice());
        products.setDescription(productDto.getDescription());
        products.setCreatedAT(createdAt);
        products.setImageFileName(storageFileName);

        repo.save(products);


        return "redirect:/products";
    }
    
}

//40:80