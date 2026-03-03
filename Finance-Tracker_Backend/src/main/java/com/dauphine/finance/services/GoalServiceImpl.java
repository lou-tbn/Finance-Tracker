package com.dauphine.finance.services;


import com.dauphine.finance.exceptions.GoalNotFoundByIdException;
import com.dauphine.finance.exceptions.UserNotFoundByIdException;
import com.dauphine.finance.model.Goal;
import com.dauphine.finance.model.User;
import com.dauphine.finance.repository.GoalRepository;
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

    public GoalServiceImpl(GoalRepository repository, UserRepository userRepository){
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Goal> getAll(){
        return repository.findAll();
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
    public Goal create(UUID userId, String title, BigDecimal targetAmount, BigDecimal currentAmount, LocalDateTime deadline){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        Goal goal = new Goal(user, title, targetAmount, currentAmount, deadline);
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

}
