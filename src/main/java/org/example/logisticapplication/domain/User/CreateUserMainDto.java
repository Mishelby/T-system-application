package org.example.logisticapplication.domain.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserMainDto extends AbstractUserInfo{
    public CreateUserMainDto(
            Integer id,
            String username,
            String password,
            String email
    ) {
        super(id, username, password, email);
    }
}
