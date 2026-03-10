package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.demo.entity.Transaction;


public interface FinanceService {
	
	List<Transaction> getAllTransactions();
	
	Transaction saveTransaction(Transaction transaction);
	Transaction getTransactionById(Long id);
	Transaction updateTransaction(Transaction transaction);
	
	void deleteTransactionById(Long id);
	
	BigDecimal calculateBalance();
	BigDecimal calculateExpense();
	BigDecimal calculateIncome();
	Map<String, BigDecimal> calculateExpenseByCategory();
	Map<String, BigDecimal> calculateIncomeByCategory();
	
	Map<String, BigDecimal> calculateExpenseByMonth();
	Map<String, BigDecimal> calculateIncomeByMonth();

}
