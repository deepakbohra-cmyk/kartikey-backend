package com.kartikey.kartikey.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String tlEmail;
    private String location;
}
