package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;

/**
 * Data Transfer Object for Transaction
 * Used to transfer data between the controller and service layer
 */
public class TransactionDTO {
    
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String productName;
    
    @NotBlank(message = "Category name is required")
    private String categoryName;
    
    @NotNull(message = "Transaction amount is required")
    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    private BigDecimal transactionAmount;
    
    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "^(income|expense)$", message = "Type must be either 'income' or 'expense'")
    private String type;
    
    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionTime;
    
    // Constructors
    public TransactionDTO() {}
    
    public TransactionDTO(Long id, String productName, String categoryName, 
                         BigDecimal transactionAmount, String type, LocalDate transactionTime) {
        this.id = id;
        this.productName = productName;
        this.categoryName = categoryName;
        this.transactionAmount = transactionAmount;
        this.type = type;
        this.transactionTime = transactionTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }
    
    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDate getTransactionTime() {
        return transactionTime;
    }
    
    public void setTransactionTime(LocalDate transactionTime) {
        this.transactionTime = transactionTime;
    }
    
    @Override
    public String toString() {
        return "TransactionDTO{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", type='" + type + '\'' +
                ", transactionTime=" + transactionTime +
                '}';
    }

    /** Builder pattern for convenient TransactionDTO construction */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String productName;
        private String categoryName;
        private BigDecimal transactionAmount;
        private String type;
        private LocalDate transactionTime;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public Builder transactionAmount(BigDecimal transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder transactionTime(LocalDate transactionTime) {
            this.transactionTime = transactionTime;
            return this;
        }

        public TransactionDTO build() {
            return new TransactionDTO(id, productName, categoryName, transactionAmount, type, transactionTime);
        }
    }
}

