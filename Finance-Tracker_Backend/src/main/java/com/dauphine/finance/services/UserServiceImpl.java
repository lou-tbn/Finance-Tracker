package com.dauphine.finance.services;

import com.dauphine.finance.exceptions.EmailAlreadyExistException;
import com.dauphine.finance.exceptions.UsernameAlreadyExistException;
import com.dauphine.finance.exceptions.UserNotFoundByIdException;
import com.dauphine.finance.model.User;
import com.dauphine.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository){
        this.repository = repository;
    }

    @Override
    public List<User> getAll(){
        return repository.findAll();
    }

    @Override
    public List<User> getAllLikeName(String username){
        return repository.findAllLikeName(username);
    }

    @Override
    public User getById(UUID id) throws UserNotFoundByIdException {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundByIdException(id));
    }
    @Override
    public User create(String username, String email, String password){

        if (repository.existsByName(username)) {
            throw new UsernameAlreadyExistException(username);
        }

        if(repository.existsByEmail(email)){
            throw new EmailAlreadyExistException(email);
        }

        User user = new User(username, email, password);
        return repository.save(user);
    }

    @Override
    public User update(UUID id, String newUsername, String newEmail, String newPassword){
        User user = getById(id);
        if (repository.existsByName(newUsername) && !newUsername.equalsIgnoreCase(user.getUsername())) {
            throw new UsernameAlreadyExistException(newUsername);
        }
        if (repository.existsByEmail(newEmail) && !newEmail.equalsIgnoreCase(user.getEmail())) {
            throw new EmailAlreadyExistException(newEmail);
        }
        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        return repository.save(user);
    }

    @Override
    public User patch(UUID id, String newUsername, String newEmail, String newPassword){
        User user = getById(id);

        if(newUsername !=null){
            if (repository.existsByName(newUsername) && !newUsername.equalsIgnoreCase(user.getUsername())) {
                throw new UsernameAlreadyExistException(newUsername);
            }
            user.setUsername(newUsername);
        }
        if(newEmail != null) {
            if (repository.existsByEmail(newEmail) && !newEmail.equalsIgnoreCase(user.getEmail())) {
                throw new EmailAlreadyExistException(newEmail);
            }
            user.setEmail(newEmail);
        }
        if(newPassword != null) {
            user.setPassword(newPassword);
        }

        return repository.save(user);
    }

    @Override
    public void deleteById(UUID id){
        User user = getById(id);
        repository.delete(user);
    }

}
