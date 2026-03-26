package com.dauphine.finance.services;


import com.dauphine.finance.exceptions.GoalNotFoundByIdException;
import com.dauphine.finance.exceptions.UserNotFoundByIdException;
import com.dauphine.finance.model.Goal;
import com.dauphine.finance.model.Transaction;
import com.dauphine.finance.model.TransactionType;
import com.dauphine.finance.model.User;
import com.dauphine.finance.repository.GoalRepository;
import com.dauphine.finance.repository.TransactionRepository;
import com.dauphine.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository repository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public GoalServiceImpl(GoalRepository repository, UserRepository userRepository, TransactionRepository transactionRepository){
        this.repository = repository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Goal> getAll(){
        return repository.findAll();
    }

    @Override
    public List<Goal> getAllByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));
        return repository.findAllByUserId(userId);
    }

    @Override
    public List<Goal> getAllLikeTitle(String title){
        return repository.findAllLikeTitle(title);
    }

    @Override
    public Goal getById(UUID id) throws GoalNotFoundByIdException {
        return repository.findById(id)
                .orElseThrow(() -> new GoalNotFoundByIdException(id));
    }

    @Override
    public Goal create(UUID userId, String title, BigDecimal targetAmount, BigDecimal currentAmount, LocalDateTime deadline) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        BigDecimal initialAmount = currentAmount != null ? currentAmount : BigDecimal.ZERO;  // ✅
        Goal goal = new Goal(user, title, targetAmount, initialAmount, deadline);
        return repository.save(goal);
    }

    @Override
    public Goal update(UUID id, UUID newUserID, String newTitle, BigDecimal newTargetAmount, BigDecimal newCurrentAmount, LocalDateTime newDeadline){
        Goal goal = getById(id);
        User user = userRepository.findById(newUserID)
                .orElseThrow(() -> new UserNotFoundByIdException(newUserID));

        goal.setUser(user);
        goal.setTitle(newTitle);
        goal.setTargetAmount(newTargetAmount);
        goal.setCurrentAmount(newCurrentAmount);
        goal.setDeadline(newDeadline);
        return repository.save(goal);
    }

    @Override
    public Goal patch(UUID id, UUID newUserID, String newTitle, BigDecimal newTargetAmount, BigDecimal newCurrentAmount, LocalDateTime newDeadline){
        Goal goal = getById(id);

        if (newUserID != null){
            User user = userRepository.findById(newUserID)
                    .orElseThrow(() -> new UserNotFoundByIdException(newUserID));
            goal.setUser(user);
        }
        if(newTitle != null) {
                goal.setTitle(newTitle);
        }
        if(newTargetAmount != null){
            goal.setTargetAmount(newTargetAmount);
        }
        if(newCurrentAmount != null){
            goal.setCurrentAmount(newCurrentAmount);
        }
        if(newDeadline != null){
            goal.setDeadline(newDeadline);
        }

        return repository.save(goal);
    }

    @Override
    public void deleteById(UUID id){
        Goal goal = getById(id);
        repository.delete(goal);
    }

    @Override
    public Goal transfer(UUID goalId, UUID userId, BigDecimal amount) {
        Goal goal = getById(goalId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalIncome = transactionRepository.sumIncomeCumulativeByUser(userId, now);
        BigDecimal totalExpense = transactionRepository.sumExpenseCumulativeByUser(userId, now);
        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
        BigDecimal balance = totalIncome.subtract(totalExpense);

        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                "Solde insuffisant : solde disponible = " + balance + " €, montant demandé = " + amount + " €."
            );
        }

        Transaction transaction = new Transaction(user, amount, now, null,
                "Virement vers \"" + goal.getTitle() + "\"", TransactionType.EXPENSE);
        transaction.setIsTemplate(false);
        transactionRepository.save(transaction);

        BigDecimal newCurrent = goal.getCurrentAmount().add(amount);
        if (newCurrent.compareTo(goal.getTargetAmount()) > 0) {
            newCurrent = goal.getTargetAmount();
        }
        goal.setCurrentAmount(newCurrent);
        return repository.save(goal);
    }

    @Override
    public Goal withdraw(UUID goalId, UUID userId, BigDecimal amount) {
        Goal goal = getById(goalId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (goal.getCurrentAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                "Fonds insuffisants dans l'objectif : disponible = " + goal.getCurrentAmount() + " €, demandé = " + amount + " €."
            );
        }

        Transaction transaction = new Transaction(user, amount, LocalDateTime.now(), null,
                "Retrait depuis \"" + goal.getTitle() + "\"", TransactionType.INCOME);
        transaction.setIsTemplate(false);
        transactionRepository.save(transaction);

        goal.setCurrentAmount(goal.getCurrentAmount().subtract(amount));
        return repository.save(goal);
    }

}
