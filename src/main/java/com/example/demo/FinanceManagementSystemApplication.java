package com.example.demo;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.demo.repository.TransactionRepository;

@SpringBootApplication
public class FinanceManagementSystemApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FinanceManagementSystemApplication.class, args);
		
	}
	
	LocalDate transactionTime = LocalDate.parse("2024-01-10");
	
	@Autowired
	private TransactionRepository transactionRepository;
	@Override
	public void run(String... args) throws Exception {
		
		/*
		Transaction transaction1 = new Transaction("Woda", "food", new BigDecimal("3.50"), "expense", transactionTime);
        transactionRepository.save(transaction1);
        
        Transaction transaction2 = new Transaction("Dentysta", "food", new BigDecimal("500.00"), "expense", transactionTime);
        transactionRepository.save(transaction2);
        */

	}

}
