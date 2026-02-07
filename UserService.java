package com.shopingsystem.service;

import com.shopingsystem.dao.UserDAO;
import com.shopingsystem.model.User;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public void registerUser(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }
        userDAO.saveUser(user);
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User getUserById(long id) {
        return userDAO.getUserById(id);
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void updateUser(User user) {
        if (user == null || user.getUserId() == 0) {
            throw new IllegalArgumentException("Invalid user for update");
        }
        userDAO.updateUser(user);
    }

    public void deleteUser(long userId) {
        userDAO.deleteUser(userId);
    }
}