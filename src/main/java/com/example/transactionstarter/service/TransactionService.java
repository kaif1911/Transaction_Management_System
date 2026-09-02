package com.example.transactionstarter.service;

import com.example.transactionstarter.entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);

    Transaction getTransaction(String transactionId);

    Transaction updateTransactionStatus(String transactionId, String newStatus);

    List<Transaction> getCustomerTransactions(String customerId);
    List<Transaction> getTransactionsByStatus(String status);

}