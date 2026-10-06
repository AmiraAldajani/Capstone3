package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTOIN.AccountDTOIn;
import com.example.ejadwebapplication.DTOOUT.UserDTOOut;
import com.example.ejadwebapplication.Model.User;
import com.example.ejadwebapplication.Repository.NotificationRepository;
import com.example.ejadwebapplication.Repository.ReportRepository;
import com.example.ejadwebapplication.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final NotificationRepository notificationRepository;

    public List<UserDTOOut> getAllUsers() {
        List<UserDTOOut> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(convertToDTO(user));
        }
        return users;
    }

    public UserDTOOut getUserById(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }
        return convertToDTO(user);
    }

    public void addUser(AccountDTOIn dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ApiException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        userRepository.save(user);
    }

    public void updateUser(Integer id, AccountDTOIn dto) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (!user.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new ApiException("Username already exists");
        }
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Email already exists");
        }

        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }
        if (reportRepository.existsByUser(user)) {
            throw new ApiException("Cannot delete a user who has reports, delete the reports first");
        }
        notificationRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }

    private UserDTOOut convertToDTO(User user) {
        return new UserDTOOut(user.getId(), user.getFullName(), user.getUsername(),
                user.getEmail(), user.getPhone(), user.getCreatedAt());
    }
}