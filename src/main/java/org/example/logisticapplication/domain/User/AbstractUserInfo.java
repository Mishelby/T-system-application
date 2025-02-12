package org.example.logisticapplication.domain.User;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "username")
public abstract class AbstractUserInfo {
    Integer id;
    String username;
    String password;
    String email;
}
