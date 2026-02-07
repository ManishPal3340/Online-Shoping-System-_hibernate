package com.shopingsystem.service;

import com.shopingsystem.dao.ProductDAO;
import com.shopingsystem.model.Product;

import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    // ────────────────────────────────────────────────
    //  CREATE
    // ────────────────────────────────────────────────
    public void addProduct(Product product) {
        if (product == null || product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        productDAO.saveProduct(product);
    }

    // ────────────────────────────────────────────────
    //  READ
    // ────────────────────────────────────────────────
    public Product getProductById(long id) {
        return productDAO.getProductById(id);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> searchProductsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDAO.searchProductsByName(keyword.trim());
    }

    // ────────────────────────────────────────────────
    //  UPDATE
    // ────────────────────────────────────────────────
    public void updateProduct(Product product) {
        if (product == null || product.getProductId() == 0) {
            throw new IllegalArgumentException("Invalid product for update");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        productDAO.updateProduct(product);
    }

    // ────────────────────────────────────────────────
    //  DELETE
    // ────────────────────────────────────────────────
    public void deleteProduct(long id) {
        productDAO.deleteProduct(id);
    }

    // Optional: check if product exists (useful before update/delete)
    public boolean existsById(long id) {
        return getProductById(id) != null;
    }
}