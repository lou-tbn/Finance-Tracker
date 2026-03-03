package com.dauphine.finance.services;

import com.dauphine.finance.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAll();

    List<User> getAllLikeName(String name);

    User getById(UUID id);

    User create(String username, String email, String password);

    User update(UUID id, String newUsername, String newEmail, String newPassword);

    User patch(UUID id, String newUsername, String newEmail, String newPassword);

    void deleteById(UUID id);
}
