package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SaveUserActivityLogCmd;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLog {
    private Long id;
    private String email;
    private String activityType;
    private String activityDescription;
    private String ipAddress;

    public UserActivityLog(SaveUserActivityLogCmd command) {
        this.id = command.getId();
        this.email = command.getEmail();
        this.activityType = command.getActivityType();
        this.activityDescription = command.getActivityDescription();
        this.ipAddress = command.getIpAddress();
    }
}
