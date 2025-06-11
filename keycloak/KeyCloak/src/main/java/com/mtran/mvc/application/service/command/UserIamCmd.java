package com.mtran.mvc.application.service.command;

import com.mtran.mvc.application.dto.response.UserDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface UserIamCmd {
    void createUser(UserDTO user);
    void updateLastChangePassword(String email, LocalDateTime lastChangePassword);
    void changePassword(String email, String oldPassword, String newPassword);
}
