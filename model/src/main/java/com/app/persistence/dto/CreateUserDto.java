package com.app.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserDto {
    private String name;
    private String surname;
    private String username;
    private String password;
    private String passwordConfirmation;
    private String email;
    private String emailConfirmation;
}
