package com.dauphine.finance.services;

import com.dauphine.finance.exceptions.TransactionNotFoundByIdException;
import com.dauphine.finance.exceptions.UserNotFoundByIdException;
import com.dauphine.finance.model.Frequency;
import com.dauphine.finance.model.Transaction;
import com.dauphine.finance.model.TransactionType;
import com.dauphine.finance.model.User;
import com.dauphine.finance.repository.TransactionRepository;
import com.dauphine.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {


    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository repository, UserRepository userRepository){
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Transaction> getAll(){
        return repository.findAll();
    }

    @Override
    public Transaction getById(UUID id) throws TransactionNotFoundByIdException {
        return repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundByIdException(id));
    }
    @Override
    public Transaction create(UUID userId, BigDecimal amount, LocalDateTime date, Frequency frequency, String description, TransactionType transactionType){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));;
        Transaction transaction = new Transaction(user, amount, date, frequency, description, transactionType);
        return repository.save(transaction);
    }

    @Override
    public Transaction update(UUID id, UUID newUserId, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType){
        Transaction transaction = getById(id);
        User user = userRepository.findById(newUserId)
                .orElseThrow(() -> new UserNotFoundByIdException(newUserId));;
        
        transaction.setUser(user);
        transaction.setAmount(newAmount);
        transaction.setDate(newDate);
        transaction.setFrequency(newFrequency);
        transaction.setDescription(newDescription);
        transaction.setTransactionType(newTransactionType);

        return repository.save(transaction);
    }

    @Override
    public Transaction patch(UUID id, UUID newUserId, BigDecimal newAmount, LocalDateTime newDate, Frequency newFrequency, String newDescription, TransactionType newTransactionType){
        Transaction transaction = getById(id);
        
        if (newUserId != null){
            User user = userRepository.findById(newUserId)
                    .orElseThrow(() -> new UserNotFoundByIdException(newUserId));
            transaction.setUser(user);
        }
        if(newAmount != null) {
            transaction.setAmount(newAmount);
        }
        if(newDate != null){
            transaction.setDate(newDate);
        }
        if(newFrequency != null){
            transaction.setFrequency(newFrequency);
        }
        if(newDescription != null){
            transaction.setDescription(newDescription);
        }
        if(newTransactionType!= null){
            transaction.setTransactionType(newTransactionType);
        }

        return repository.save(transaction);
    }

    @Override
    public void deleteById(UUID id){
        Transaction transaction = getById(id);
        repository.delete(transaction);
    }

}
