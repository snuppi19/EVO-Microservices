package com.mtran.mvc.domain;

import com.mtran.mvc.domain.command.SaveInvalidTokenCmd;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class InvalidateToken {
    String id;
    Date expiryTime;

    public InvalidateToken(SaveInvalidTokenCmd command) {
        this.id = command.getId();
        this.expiryTime = command.getExpiryTime();
    }
}
