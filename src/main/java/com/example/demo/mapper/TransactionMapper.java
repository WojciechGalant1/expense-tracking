package com.example.demo.mapper;

import com.example.demo.dto.TransactionDTO;
import com.example.demo.entity.Transaction;

/**
 * Mapper class to convert between Transaction entity and TransactionDTO
 */
public class TransactionMapper {
    
    /**
     * Convert Transaction entity to TransactionDTO
     */
    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        return TransactionDTO.builder()
                .id(transaction.getId())
                .productName(transaction.getProductName())
                .categoryName(transaction.getCategoryName())
                .transactionAmount(transaction.getTransactionAmount())
                .type(transaction.getType())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }
    
    /**
     * Convert TransactionDTO to Transaction entity
     */
    public static Transaction toEntity(TransactionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setProductName(dto.getProductName());
        transaction.setCategoryName(dto.getCategoryName());
        transaction.setTransactionAmount(dto.getTransactionAmount());
        transaction.setType(dto.getType());
        transaction.setTransactionTime(dto.getTransactionTime());
        
        return transaction;
    }
    
    /**
     * Update existing Transaction entity with data from TransactionDTO
     */
    public static void updateEntityFromDTO(TransactionDTO dto, Transaction transaction) {
        if (dto == null || transaction == null) {
            return;
        }
        
        transaction.setProductName(dto.getProductName());
        transaction.setCategoryName(dto.getCategoryName());
        transaction.setTransactionAmount(dto.getTransactionAmount());
        transaction.setType(dto.getType());
        transaction.setTransactionTime(dto.getTransactionTime());
    }
}

