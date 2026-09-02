package com.example.transactionstarter.service;

import com.example.transactionstarter.entity.Transaction;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.exception.InvalidStatusTransitionException;
import com.example.transactionstarter.exception.TransactionNotFoundException;
import com.example.transactionstarter.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    // ------------------------------------------------
    // 1. Create transaction successfully
    // ------------------------------------------------

    @Test
    void createTransaction_success() {

        Transaction transaction = new Transaction(
                "TXN101",
                "CUST001",
                new BigDecimal("1500.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        when(transactionRepository.existsById("TXN101"))
                .thenReturn(false);

        when(transactionRepository.save(transaction))
                .thenReturn(transaction);

        Transaction result =
                transactionService.createTransaction(transaction);

        assertNotNull(result);
        assertEquals("TXN101", result.getTransactionId());
        assertEquals("CUST001", result.getCustomerId());

        verify(transactionRepository)
                .save(transaction);
    }


    // ------------------------------------------------
    // 2. Duplicate transaction ID
    // ------------------------------------------------

    @Test
    void createTransaction_duplicateId_throwsException() {

        Transaction transaction = new Transaction(
                "TXN001",
                "CUST001",
                new BigDecimal("1500.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        when(transactionRepository.existsById("TXN001"))
                .thenReturn(true);

        assertThrows(
                DuplicateTransactionException.class,
                () -> transactionService
                        .createTransaction(transaction)
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // ------------------------------------------------
    // 3. Get transaction successfully
    // ------------------------------------------------

    @Test
    void getTransaction_success() {

        Transaction transaction = new Transaction(
                "TXN001",
                "CUST001",
                new BigDecimal("1500.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        when(transactionRepository.findById("TXN001"))
                .thenReturn(Optional.of(transaction));

        Transaction result =
                transactionService.getTransaction("TXN001");

        assertNotNull(result);
        assertEquals(
                "TXN001",
                result.getTransactionId()
        );

        verify(transactionRepository)
                .findById("TXN001");
    }


    // ------------------------------------------------
    // 4. Transaction not found
    // ------------------------------------------------

    @Test
    void getTransaction_notFound_throwsException() {

        when(transactionRepository.findById("TXN999"))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService
                        .getTransaction("TXN999")
        );

        verify(transactionRepository)
                .findById("TXN999");
    }


    // ------------------------------------------------
    // 5. Valid status transition
    // ------------------------------------------------

    @Test
    void updateStatus_validTransition_success() {

        Transaction transaction = new Transaction(
                "TXN001",
                "CUST001",
                new BigDecimal("1500.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        when(transactionRepository.findById("TXN001"))
                .thenReturn(Optional.of(transaction));

        when(transactionRepository.save(transaction))
                .thenReturn(transaction);

        Transaction result =
                transactionService.updateTransactionStatus(
                        "TXN001",
                        "COMPLETED"
                );

        assertEquals(
                "COMPLETED",
                result.getTransactionStatus()
        );

        verify(transactionRepository)
                .save(transaction);
    }


    // ------------------------------------------------
    // 6. Invalid status transition
    // ------------------------------------------------

    @Test
    void updateStatus_invalidTransition_throwsException() {

        Transaction transaction = new Transaction(
                "TXN002",
                "CUST001",
                new BigDecimal("2000.00"),
                "INR",
                "PAYMENT",
                "COMPLETED"
        );

        when(transactionRepository.findById("TXN002"))
                .thenReturn(Optional.of(transaction));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> transactionService
                        .updateTransactionStatus(
                                "TXN002",
                                "PENDING"
                        )
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // ------------------------------------------------
    // 7. Status update - transaction not found
    // ------------------------------------------------

    @Test
    void updateStatus_transactionNotFound_throwsException() {

        when(transactionRepository.findById("TXN999"))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService
                        .updateTransactionStatus(
                                "TXN999",
                                "COMPLETED"
                        )
        );

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }


    // ------------------------------------------------
    // 8. Customer transaction lookup
    // ------------------------------------------------

    @Test
    void getCustomerTransactions_success() {

        Transaction transaction1 = new Transaction(
                "TXN001",
                "CUST001",
                new BigDecimal("1000.00"),
                "INR",
                "PAYMENT",
                "PENDING"
        );

        Transaction transaction2 = new Transaction(
                "TXN002",
                "CUST001",
                new BigDecimal("2000.00"),
                "INR",
                "PAYMENT",
                "COMPLETED"
        );

        when(transactionRepository
                .findByCustomerId("CUST001"))
                .thenReturn(List.of(transaction1, transaction2));

        List<Transaction> result =
                transactionService
                        .getCustomerTransactions("CUST001");

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "TXN001",
                result.get(0).getTransactionId()
        );

        assertEquals(
                "TXN002",
                result.get(1).getTransactionId()
        );

        verify(transactionRepository)
                .findByCustomerId("CUST001");
    }

}