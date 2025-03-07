package com.example.Controller;

import com.example.model.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private static final double FULL_PERCENTAGE = 100.0;

    @Autowired
    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public Product addProduct(@RequestBody final Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product must have a Name");
        }

        if (product.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Price must be greater than or equal to 0");
        }

        return productService.addProduct(product);
    }

    @GetMapping("/")
    public ArrayList<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable final UUID productId) {
        Product product = productService.getProductById(productId);

        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Product with id %s not found", productId));
        }

        return product;
    }

    @PutMapping("/update/{productId}")
    public Product updateProduct(@PathVariable final UUID productId,
                                 @RequestBody final Map<String, Object> body) {
        // "newPrice" could be stored as an Integer or Double depending on
        // the JSON input, Casting to Number ensures safe conversion
        String newName = (String) body.get("newName");
        Number newPriceObj = (Number) body.get("newPrice");

        if (newName == null || newName.isEmpty() || newPriceObj == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product must have a Name and Price");
        }

        double newPrice = newPriceObj.doubleValue();

        if (newPrice < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Price must be greater than or equal to 0");
        }

        Product updatedProduct = productService.updateProduct(productId,
                newName, newPrice);

        if (updatedProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Product with id %s not found", productId));
        }

        return updatedProduct;
    }

    @PutMapping("/applyDiscount")
    public String applyDiscount(@RequestParam final double discount,
                                @RequestBody final ArrayList<UUID>
            productIds) {
        if (discount < 0 || discount > FULL_PERCENTAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Discount must be between 0 and 100");
        }

        productService.applyDiscount(discount, productIds);
        return "Discount Applied";
    }
}

