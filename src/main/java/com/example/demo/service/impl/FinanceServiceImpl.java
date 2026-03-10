package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Transaction;
import com.example.demo.exception.DatabaseOperationException;
import com.example.demo.exception.InvalidTransactionDataException;
import com.example.demo.exception.TransactionNotFoundException;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.FinanceService;

@Service
public class FinanceServiceImpl implements FinanceService {

    private static final String TYPE_INCOME = "income";
    private static final String TYPE_EXPENSE = "expense";

    private final TransactionRepository transactionRepository;

    public FinanceServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // ======================================================
    // ===============   UTILITY METHODS   ==================
    // ======================================================

    /** Generic wrapper for database operations */
    private <T> T safeExecute(String message, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DatabaseOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException(message, e);
        }
    }

    /** Filter transactions by type */
    private List<Transaction> getTransactionsByType(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(t -> type.equals(t.getType()))
                .collect(Collectors.toList());
    }

    /** Sum amounts from transactions */
    private BigDecimal calculateTotalAmount(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Group transactions by a given key and sum their amounts */
    private Map<String, BigDecimal> aggregateByKey(List<Transaction> transactions, Function<Transaction, String> keyExtractor) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        keyExtractor,
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getTransactionAmount, BigDecimal::add)
                ));
    }

    /** Validate transaction before save/update */
    private void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new InvalidTransactionDataException("Transaction cannot be null");
        }
        if (transaction.getProductName() == null || transaction.getProductName().trim().isEmpty()) {
            throw new InvalidTransactionDataException("Product name cannot be null or empty");
        }
        if (transaction.getCategoryName() == null || transaction.getCategoryName().trim().isEmpty()) {
            throw new InvalidTransactionDataException("Category name cannot be null or empty");
        }
        if (transaction.getTransactionAmount() == null || transaction.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionDataException("Transaction amount must be positive");
        }
        if (transaction.getType() == null ||
                (!transaction.getType().equals(TYPE_INCOME) && !transaction.getType().equals(TYPE_EXPENSE))) {
            throw new InvalidTransactionDataException("Transaction type must be 'income' or 'expense'");
        }
        if (transaction.getTransactionTime() == null) {
            throw new InvalidTransactionDataException("Transaction time cannot be null");
        }
    }

    // ======================================================
    // ================   CRUD OPERATIONS   =================
    // ======================================================

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        validateTransaction(transaction);
        return safeExecute("Failed to save transaction",
                () -> transactionRepository.save(transaction));
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidTransactionDataException("Invalid transaction ID: " + id);
        }

        return safeExecute("Failed to retrieve transaction with id: " + id, () ->
                transactionRepository.findById(id)
                        .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id))
        );
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Transaction transaction) {
        if (transaction == null || transaction.getId() == null) {
            throw new InvalidTransactionDataException("Transaction or transaction ID cannot be null");
        }
        validateTransaction(transaction);

        return safeExecute("Failed to update transaction with id: " + transaction.getId(),
                () -> transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void deleteTransactionById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidTransactionDataException("Invalid transaction ID: " + id);
        }

        safeExecute("Failed to delete transaction with id: " + id, () -> {
            if (!transactionRepository.existsById(id)) {
                throw new TransactionNotFoundException("Transaction not found with id: " + id);
            }
            transactionRepository.deleteById(id);
            return null;
        });
    }

    // ======================================================
    // ==============   CALCULATION METHODS   ===============
    // ======================================================

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateBalance() {
        return safeExecute("Failed to calculate balance", () -> {
            List<Transaction> all = getAllTransactions();
            BigDecimal income = calculateTotalAmount(getTransactionsByType(all, TYPE_INCOME));
            BigDecimal expense = calculateTotalAmount(getTransactionsByType(all, TYPE_EXPENSE));
            return income.subtract(expense);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateIncome() {
        return safeExecute("Failed to calculate income", () -> {
            List<Transaction> all = getAllTransactions();
            return calculateTotalAmount(getTransactionsByType(all, TYPE_INCOME));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateExpense() {
        return safeExecute("Failed to calculate expenses", () -> {
            List<Transaction> all = getAllTransactions();
            return calculateTotalAmount(getTransactionsByType(all, TYPE_EXPENSE));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateExpenseByCategory() {
        return safeExecute("Failed to calculate expenses by category", () -> {
            List<Transaction> all = getAllTransactions();
            return aggregateByKey(getTransactionsByType(all, TYPE_EXPENSE), Transaction::getCategoryName);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateIncomeByCategory() {
        return safeExecute("Failed to calculate income by category", () -> {
            List<Transaction> all = getAllTransactions();
            return aggregateByKey(getTransactionsByType(all, TYPE_INCOME), Transaction::getCategoryName);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateExpenseByMonth() {
        return safeExecute("Failed to calculate expenses by month", () -> {
            List<Transaction> all = getAllTransactions();
            return aggregateByKey(getTransactionsByType(all, TYPE_EXPENSE),
                    t -> t.getTransactionTime().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateIncomeByMonth() {
        return safeExecute("Failed to calculate income by month", () -> {
            List<Transaction> all = getAllTransactions();
            return aggregateByKey(getTransactionsByType(all, TYPE_INCOME),
                    t -> t.getTransactionTime().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        });
    }
}
