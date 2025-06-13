package com.mtran.mvc.application.service.command;

import com.mtran.common.support.ActivityType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserActivityLogCmd {
    void logActivity(String email, ActivityType activityType, String description, HttpServletRequest request);
}
