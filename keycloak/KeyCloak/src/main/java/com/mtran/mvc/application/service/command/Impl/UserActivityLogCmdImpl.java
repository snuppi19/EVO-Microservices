package com.mtran.mvc.application.service.command.Impl;

import com.mtran.common.support.ActivityType;
import com.mtran.mvc.application.service.command.UserActivityLogCmd;
import com.mtran.mvc.domain.UserActivityLog;
import com.mtran.mvc.domain.command.SaveUserActivityLogCmd;
import com.mtran.mvc.domain.repository.UserActivityLogDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.UserActivityLogEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityLogCmdImpl implements UserActivityLogCmd {
    private final UserActivityLogDomainRepository userActivityLogDomainRepository;

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    @Override
    public void logActivity(String email, ActivityType activityType, String description, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        SaveUserActivityLogCmd cmd = SaveUserActivityLogCmd.builder()
                                                           .email(email)
                                                            .activityType(activityType.name())
                                                           .activityDescription(description)
                                                           .ipAddress(ipAddress)
                                                           .build();
        UserActivityLog a=new UserActivityLog(cmd);
        userActivityLogDomainRepository.save(a);
    }
}
