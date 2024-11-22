package io.mountblue.service;

import io.mountblue.dao.UserRepository;
import io.mountblue.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
         userRepository.deleteById(id);
    }

    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }


    public User findById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email); // Find user by email
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByUsername(String authorName) {
        return userRepository.findByName(authorName);
    }

    public User getUserByEmail(String username) {
        return userRepository.findByEmail(username);
    }
}
