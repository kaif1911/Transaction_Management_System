package com.example.transactionstarter.controller;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 1. Create Transaction
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        Transaction transaction = new Transaction();

        transaction.setTransactionId(request.getTransactionId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setTransactionStatus(request.getTransactionStatus());

        Transaction createdTransaction =
                transactionService.createTransaction(transaction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTransaction);
    }

    // 2. Get Transaction
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(
            @PathVariable String transactionId) {

        Transaction transaction =
                transactionService.getTransaction(transactionId);

        return ResponseEntity.ok(transaction);
    }

    // 3. Update Transaction Status
    @PatchMapping("/{transactionId}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable String transactionId,
            @RequestParam String status) {

        Transaction updatedTransaction =
                transactionService.updateTransactionStatus(
                        transactionId,
                        status);

        return ResponseEntity.ok(updatedTransaction);
    }

    // 4. Get Customer Transactions
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Transaction>> getCustomerTransactions(
            @PathVariable String customerId) {

        List<Transaction> transactions =
                transactionService.getCustomerTransactions(customerId);

        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByStatus(status)
        );
    }

}

