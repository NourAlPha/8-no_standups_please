package com.example.controller;

import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody final Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable final UUID productId) {
        return productService.getProductById(productId);
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable final UUID productId,
                                 @RequestBody final Map<String, Object> body) {
        // "newPrice" could be stored as an Integer or Double depending on
        // the JSON input, Casting to Number ensures safe conversion
        String newName = (String) body.get("newName");
        Number newPriceObj = (Number) body.get("newPrice");
        double newPrice = newPriceObj.doubleValue();

        return productService.updateProduct(productId, newName, newPrice);
    }

    @DeleteMapping("/delete/{productId}")
    public String deleteProductById(@PathVariable UUID productId){
        productService.deleteProductById(productId);
        return "Product deleted successfully";
    }

    @PutMapping("/applyDiscount")
    public String applyDiscount(@RequestParam final double discount,
                                @RequestBody final ArrayList<UUID>
            productIds) {
        productService.applyDiscount(discount, productIds);
        return "Discount Applied";
    }
}

