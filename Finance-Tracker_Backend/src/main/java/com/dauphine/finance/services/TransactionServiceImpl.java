package com.dauphine.finance.services;

import com.dauphine.finance.exceptions.CategoryNotFoundByIdException;
import com.dauphine.finance.exceptions.TransactionNotFoundByIdException;
import com.dauphine.finance.exceptions.UserNotFoundByIdException;
import com.dauphine.finance.model.*;
import com.dauphine.finance.repository.CategoryRepository;
import com.dauphine.finance.repository.TransactionRepository;
import com.dauphine.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionServiceImpl(TransactionRepository repository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Transaction> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Transaction> getAllWithFilters(
            UUID userId,
            UUID categoryId,
            LocalDateTime start,
            LocalDateTime end,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            TransactionType transactionType,
            String search
    ) {
        List<Transaction> transactions;
        if (userId != null) {
            userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundByIdException(userId));
            transactions = repository.findAllByUserId(userId);
        } else {
            transactions = repository.findAll();
        }

        if (categoryId != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getCategory() != null && categoryId.equals(t.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (start != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getDate() != null && !t.getDate().isBefore(start))
                    .collect(Collectors.toList());
        }
        if (end != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getDate() != null && !t.getDate().isAfter(end))
                    .collect(Collectors.toList());
        }
        if (minAmount != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getAmount() != null && t.getAmount().compareTo(minAmount) >= 0)
                    .collect(Collectors.toList());
        }
        if (maxAmount != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getAmount() != null && t.getAmount().compareTo(maxAmount) <= 0)
                    .collect(Collectors.toList());
        }
        if (transactionType != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getTransactionType() == transactionType)
                    .collect(Collectors.toList());
        }
        if (search != null && !search.isBlank()) {
            String lowered = search.toLowerCase();
            transactions = transactions.stream()
                    .filter(t -> t.getDescription() != null && t.getDescription().toLowerCase().contains(lowered))
                    .collect(Collectors.toList());
        }
        return transactions;
    }

    @Override
    public List<Transaction> getAllByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));
        return repository.findAllByUserId(userId);
    }

    @Override
    public List<Transaction> getAllByUserIdAndCategoryId(UUID userId, UUID categoryId) {
        return repository.findAllByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Transaction> generateRecurringTransactions(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        int month = LocalDateTime.now().getMonthValue();
        int year = LocalDateTime.now().getYear();

        List<Transaction> templates = repository.findAllTemplatesByUserId(userId);
        List<Transaction> generated = new ArrayList<>();

        for (Transaction template : templates) {
            // Vérifier si déjà générée ce mois-ci
            boolean alreadyGenerated = repository.existsGeneratedTransactionThisMonth(
                    userId,
                    template.getDescription(),
                    template.getAmount(),
                    month,
                    year
            );

            if (!alreadyGenerated) {
                Transaction newTransaction = new Transaction(
                        template.getUser(),
                        template.getAmount(),
                        LocalDateTime.now(),
                        template.getFrequency(),
                        template.getDescription(),
                        template.getTransactionType()
                );
                newTransaction.setCategory(template.getCategory());
                newTransaction.setIsTemplate(false);  // c'est une occurrence, pas un template
                generated.add(repository.save(newTransaction));
            }
        }

        return generated;
    }

    @Override
    public List<Transaction> getAllByUserIdAndDateBetween(UUID userId, LocalDateTime start, LocalDateTime end) {
        return repository.findAllByUserIdAndDateBetween(userId, start, end);
    }

    @Override
    public List<Transaction> getAllByUserIdAndAmountBetween(UUID userId, BigDecimal min, BigDecimal max) {
        return repository.findAllByUserIdAndAmountBetween(userId, min, max);
    }

    @Override
    public List<Transaction> getAllByUserIdAndTransactionType(UUID userId, TransactionType type) {
        return repository.findAllByUserIdAndTransactionType(userId, type);
    }


    @Override
    public Transaction getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundByIdException(id));
    }

    @Override
    public Transaction create(UUID userId, UUID categoryId, BigDecimal amount, LocalDateTime date, Frequency frequency, String description, TransactionType transactionType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundByIdException(categoryId));
        }

        Transaction transaction = new Transaction(user, amount, date, frequency, description, transactionType);
        transaction.setCategory(category);
        return repository.save(transaction);
    }

    @Override
    public Transaction update(UUID id, UUID newUserId, UUID newCategoryId, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType) {
        Transaction transaction = getById(id);

        User user = userRepository.findById(newUserId)
                .orElseThrow(() -> new UserNotFoundByIdException(newUserId));
        transaction.setUser(user);

        if (newCategoryId != null) {
            Category category = categoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> new CategoryNotFoundByIdException(newCategoryId));
            transaction.setCategory(category);
        } else {
            transaction.setCategory(null);
        }

        transaction.setAmount(newAmount);
        transaction.setDate(newDate);
        transaction.setFrequency(newFrequency);
        transaction.setDescription(newDescription);
        transaction.setTransactionType(newTransactionType);

        return repository.save(transaction);
    }

    @Override
    public Transaction patch(UUID id, UUID newUserId, UUID newCategoryId, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType) {
        Transaction transaction = getById(id);

        if (newUserId != null) {
            User user = userRepository.findById(newUserId)
                    .orElseThrow(() -> new UserNotFoundByIdException(newUserId));
            transaction.setUser(user);
        }
        if (newCategoryId != null) {
            Category category = categoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> new CategoryNotFoundByIdException(newCategoryId));
            transaction.setCategory(category);
        }
        if (newAmount != null) {
            transaction.setAmount(newAmount);
        }
        if (newDate != null) {
            transaction.setDate(newDate);
        }
        if (newFrequency != null) {
            transaction.setFrequency(newFrequency);
        }
        if (newDescription != null) {
            transaction.setDescription(newDescription);
        }
        if (newTransactionType != null) {
            transaction.setTransactionType(newTransactionType);
        }

        return repository.save(transaction);
    }

    @Override
    public void deleteById(UUID id) {
        Transaction transaction = getById(id);
        repository.delete(transaction);
    }
}