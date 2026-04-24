package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "transaction")
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "product_name", nullable = false)
	private String productName;
	
	@Column(name = "category_name", nullable = false)
	private String categoryName;
	
	@Column(name = "transaction_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal transactionAmount;
	
	@Column(name = "type", nullable = false)
	private String type;
	
	@Column(name = "transaction_time", nullable = false)
	private LocalDate transactionTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	public Transaction() {}
	
	public Transaction(String productName, String categoryName, BigDecimal transactionAmount, String type, LocalDate transactionTime) {
		super();
		this.productName = productName;
		this.categoryName = categoryName;
		this.transactionAmount = transactionAmount;
		this.type = type;
		this.transactionTime = transactionTime;
	}
	
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
