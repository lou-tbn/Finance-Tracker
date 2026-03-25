package com.dauphine.finance.controllers;

import com.dauphine.finance.DTO.TransactionRequest;
import com.dauphine.finance.model.Transaction;
import com.dauphine.finance.model.TransactionType;
import com.dauphine.finance.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(
        name = "Transaction API",
        description = "Transactions endpoints"
)
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get all transactions",
            description = "Retrieve transactions with optional filters (userId, categoryId, date range, amount range, type)"
    )
    public ResponseEntity<List<Transaction>> getAll(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(service.getAllWithFilters(
                userId,
                categoryId,
                start,
                end,
                minAmount,
                maxAmount,
                transactionType,
                search
        ));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get transaction by id",
            description = "Retrieve a transaction with {id} by path variable"
    )
    public ResponseEntity<Transaction> getTransactionById(
            @Parameter(description = "Transaction's id")
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/generate-recurring/{userId}")
    @Operation(
            summary = "Generate recurring transactions",
            description = "Generates this month's transactions from recurring templates for a given user"
        )
     public ResponseEntity<List<Transaction>> generateRecurring(@PathVariable UUID userId) {
            List<Transaction> generated = service.generateRecurringTransactions(userId);
            return ResponseEntity.ok(generated);
        }

    @PostMapping
    @Operation(
            summary = "Create a new transaction",
            description = "Create a new transaction with user, category, amount, date, frequency, description and type"
    )
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = service.create(
                request.getUserId(),
                request.getCategoryId(),
                request.getAmount(),
                request.getDate(),
                request.getFrequency(),
                request.getDescription(),
                request.getTransactionType()
        );
        return ResponseEntity
                .created(URI.create("/v1/transactions/" + transaction.getId()))
                .body(transaction);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a Transaction",
            description = "Update all fields of a transaction identified by {id}"
    )
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request
    ) {
        Transaction updated = service.update(
                id,
                request.getUserId(),
                request.getCategoryId(),
                request.getAmount(),
                request.getDate(),
                request.getFrequency(),
                request.getDescription(),
                request.getTransactionType()
        );
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Patch a Transaction",
            description = "Partially update a transaction identified by {id}"
    )
    public ResponseEntity<Transaction> patchTransaction(
            @PathVariable UUID id,
            @RequestBody TransactionRequest request
    ) {
        Transaction patched = service.patch(
                id,
                request.getUserId(),
                request.getCategoryId(),
                request.getAmount(),
                request.getDate(),
                request.getFrequency(),
                request.getDescription(),
                request.getTransactionType()
        );
        return ResponseEntity.ok(patched);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a Transaction",
            description = "Delete a transaction identified by {id}"
    )
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}