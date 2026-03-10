package com.example.demo.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.TransactionDTO;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.*;
import com.example.demo.mapper.TransactionMapper;
import com.example.demo.service.FinanceService;

import jakarta.validation.Valid;

@Controller
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/")
    public String redirectToTransactions() {
        return "redirect:/transactions";
    }

    @GetMapping("/transactions")
    public String listTransactions(Model model) {
        try {
            List<Transaction> transactions = financeService.getAllTransactions();
            List<TransactionDTO> transactionDTOs = transactions.stream()
                    .map(TransactionMapper::toDTO)
                    .collect(Collectors.toList());
            model.addAttribute("transactions", transactionDTOs);
            model.addAttribute("balance", financeService.calculateBalance());
        } catch (DatabaseOperationException e) {
            model.addAttribute("errorMessage", "Failed to load transactions. Please try again later.");
            model.addAttribute("transactions", Collections.emptyList());
            model.addAttribute("balance", BigDecimal.ZERO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("transactions", Collections.emptyList());
            model.addAttribute("balance", BigDecimal.ZERO);
        }
        return "transactions";
    }

    @GetMapping("/transactions/new")
    public String createTransactionForm(Model model) {
        model.addAttribute("transaction", new TransactionDTO());
        return "create_transaction";
    }

    @PostMapping("/transactions")
    public String saveTransaction(@Valid @ModelAttribute("transaction") TransactionDTO transactionDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "create_transaction";
        }
        try {
            Transaction transaction = TransactionMapper.toEntity(transactionDTO);
            financeService.saveTransaction(transaction);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction created successfully!");
            return "redirect:/transactions";
        } catch (InvalidTransactionDataException | DatabaseOperationException e) {
            return handleTransactionError(e, redirectAttributes, "/transactions/new");
        }
    }

    @GetMapping("/transactions/edit/{id}")
    public String editTransactionForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Transaction transaction = financeService.getTransactionById(id);
            TransactionDTO transactionDTO = TransactionMapper.toDTO(transaction);
            model.addAttribute("transaction", transactionDTO);
            return "edit_transaction";
        } catch (TransactionNotFoundException | InvalidTransactionDataException | DatabaseOperationException e) {
            return handleTransactionError(e, redirectAttributes, "/transactions");
        }
    }

    @PostMapping("/transactions/{id}")
    public String updateTransaction(@PathVariable Long id,
                                    @Valid @ModelAttribute("transaction") TransactionDTO transactionDTO,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "edit_transaction";
        }
        try {
            Transaction existingTransaction = financeService.getTransactionById(id);
            TransactionMapper.updateEntityFromDTO(transactionDTO, existingTransaction);
            financeService.updateTransaction(existingTransaction);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
            return "redirect:/transactions";
        } catch (TransactionNotFoundException e) {
            return handleTransactionError(e, redirectAttributes, "/transactions");
        } catch (InvalidTransactionDataException | DatabaseOperationException e) {
            return handleTransactionError(e, redirectAttributes, "/transactions/edit/" + id);
        }
    }

    @PostMapping("/transactions/{id}/delete")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            financeService.deleteTransactionById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction deleted successfully!");
        } catch (TransactionNotFoundException | InvalidTransactionDataException | DatabaseOperationException e) {
            return handleTransactionError(e, redirectAttributes, "/transactions");
        }
        return "redirect:/transactions";
    }

    @GetMapping("/income")
    public String listIncome(Model model) {
        return prepareFinancialSummary(model, "income");
    }

    @GetMapping("/expenses")
    public String listExpenses(Model model) {
        return prepareFinancialSummary(model, "expense");
    }


    private String prepareFinancialSummary(Model model, String type) {
        try {
            List<Transaction> transactions = financeService.getAllTransactions();
            List<TransactionDTO> transactionDTOs = transactions.stream()
                    .map(TransactionMapper::toDTO)
                    .collect(Collectors.toList());

            Map<String, BigDecimal> byCategory = type.equals("income")
                    ? financeService.calculateIncomeByCategory()
                    : financeService.calculateExpenseByCategory();

            Map<String, BigDecimal> byMonth = type.equals("income")
                    ? financeService.calculateIncomeByMonth()
                    : financeService.calculateExpenseByMonth();

            BigDecimal total = type.equals("income")
                    ? financeService.calculateIncome()
                    : financeService.calculateExpense();

            Map<String, Long> percentageByCategory = calculatePercentages(byCategory, total);
            Map<String, Long> percentageByMonth = calculatePercentages(byMonth, total);

            model.addAttribute("transactions", transactionDTOs);
            model.addAttribute(type + "Sum", total);
            model.addAttribute(type + "ByCategory", byCategory);
            model.addAttribute(type + "ByMonth", byMonth);
            model.addAttribute("percentageByCategory", percentageByCategory);
            model.addAttribute("percentageByMonth", percentageByMonth);

            return type.equals("income") ? "income" : "expenses";
        } catch (DatabaseOperationException e) {
            model.addAttribute("errorMessage", "Failed to load " + type + " data. Please try again later.");
            return type.equals("income") ? "income" : "expenses";
        }
    }

    private String handleTransactionError(Exception e, RedirectAttributes redirectAttributes, String redirectPath) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:" + redirectPath;
    }

    private Map<String, Long> calculatePercentages(Map<String, BigDecimal> data, BigDecimal total) {
        Map<String, Long> result = new HashMap<>();
        if (total.compareTo(BigDecimal.ZERO) <= 0) return result;
        data.forEach((key, value) -> {
            BigDecimal percentage = value.multiply(BigDecimal.valueOf(100))
                    .divide(total, 0, RoundingMode.HALF_UP);
            result.put(key, percentage.longValue());
        });
        return result;
    }
}
