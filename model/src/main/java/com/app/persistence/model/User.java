package com.app.persistence.model;

import com.app.persistence.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class User {
    private Integer id;
    private String name;
    private String surname;
    private String username;
    private String password;
    private String email;
    private Role userRole;

}
