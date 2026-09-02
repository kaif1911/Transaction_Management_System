package com.example.transactionstarter.service;

import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.InvalidStatusTransitionException;
import com.example.transactionstarter.exception.InvalidTransactionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TransactionServiceImpl implements TransactionService {

    // Allowed values
    private static final Set<String> ALLOWED_CURRENCIES =
            Set.of("INR", "USD", "EUR");

    private static final Set<String> ALLOWED_TYPES =
            Set.of("PAYMENT", "REFUND");

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("PENDING", "COMPLETED", "FAILED");


    private final TransactionRepository transactionRepository;


    // Constructor
    public TransactionServiceImpl(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }


    // Create Transaction
    @Override
    public Transaction createTransaction(Transaction transaction) {

        // Business validation
        validateBusinessRules(transaction);

        // Duplicate ID check
        if (transactionRepository.existsById(
                transaction.getTransactionId())) {

            throw new DuplicateTransactionException(
                    "Transaction already exists: "
                            + transaction.getTransactionId());
        }

        return transactionRepository.save(transaction);
    }


    // Get Transaction
    @Override
    public Transaction getTransaction(String transactionId) {

        return transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found: "
                                        + transactionId));
    }


    // Update Status
    @Override
    public Transaction updateTransactionStatus(
            String transactionId,
            String newStatus) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new TransactionNotFoundException(
                                        "Transaction not found: "
                                                + transactionId));

        String currentStatus =
                transaction.getTransactionStatus();

        // Check if new status is valid
        if (!ALLOWED_STATUSES.contains(
                newStatus.toUpperCase())) {

            throw new IllegalArgumentException(
                    "Invalid status: " + newStatus);
        }

        // Check status transition
        if (!isValidStatusTransition(
                currentStatus,
                newStatus)) {

            throw new InvalidStatusTransitionException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + newStatus);
        }

        transaction.setTransactionStatus(
                newStatus.toUpperCase());

        return transactionRepository.save(transaction);
    }
    @Override
    public List<Transaction> getTransactionsByStatus(String status) {

        if (!ALLOWED_STATUSES.contains(status.toUpperCase())) {
            throw new InvalidTransactionException(
                    "Unsupported transaction status: " + status);
        }

        return transactionRepository
                .findByTransactionStatus(status.toUpperCase());
    }

    // Customer Transactions
    @Override
    public List<Transaction> getCustomerTransactions(
            String customerId) {

        return transactionRepository
                .findByCustomerId(customerId);
    }


    // Business validation
    private void validateBusinessRules(
            Transaction transaction) {

        if (!ALLOWED_CURRENCIES.contains(
                transaction.getCurrency().toUpperCase())) {

            throw new IllegalArgumentException(
                    "Unsupported currency: "
                            + transaction.getCurrency());
        }

        if (!ALLOWED_TYPES.contains(
                transaction.getTransactionType().toUpperCase())) {

            throw new IllegalArgumentException(
                    "Unsupported transaction type: "
                            + transaction.getTransactionType());
        }

        if (!ALLOWED_STATUSES.contains(
                transaction.getTransactionStatus().toUpperCase())) {

            throw new IllegalArgumentException(
                    "Unsupported transaction status: "
                            + transaction.getTransactionStatus());
        }
    }


    // Status transition rules
    private boolean isValidStatusTransition(
            String currentStatus,
            String newStatus) {

        if ("PENDING".equalsIgnoreCase(currentStatus)) {

            return "COMPLETED".equalsIgnoreCase(newStatus)
                    || "FAILED".equalsIgnoreCase(newStatus);
        }

        return false;
    }
}