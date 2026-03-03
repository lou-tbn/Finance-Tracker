package com.dauphine.finance.controllers;

import com.dauphine.finance.DTO.TransactionRequest;
import com.dauphine.finance.model.Transaction;
import com.dauphine.finance.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    public TransactionController(TransactionService service){
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get all transactions",
            description = "Retrieve all transactions or filter like name"
    )
    public ResponseEntity<List<Transaction>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get transaction by id",
            description = "Retrieve a transaction with {id} by path variable"
    )
    public ResponseEntity<Transaction> getTransactionById(
            @Parameter(description = "Transaction's id")
            @PathVariable UUID id
    ){
        Transaction transaction = service.getById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping()
    @Operation(
            summary = "Creat a new transaction",
            description = "Creat a new transaction, only require field name of the transaction to create"
    )
    public ResponseEntity<Transaction> CreatTransaction(@Valid @RequestBody TransactionRequest request){
        Transaction transaction = service.create(request.getUser(), request.getAmount(), request.getDate(), request.getFrequency(), request.getDescription(), request.getTransactionType());
        return ResponseEntity
                .created(URI.create("/v1/transactions/" + transaction.getId()))
                .body(transaction);
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Update a Transaction",
            description =  "Update the name of a transaction identified by {id}"
    )
    public ResponseEntity<Transaction> updateTransaction(@PathVariable UUID id, @RequestBody TransactionRequest request){
        Transaction updatedTransaction = service.update(id, request.getUser(), request.getAmount(), request.getDate(), request.getFrequency(), request.getDescription(), request.getTransactionType());
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete a Transaction",
            description = "Delete a Transaction by {id}"
    )
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
