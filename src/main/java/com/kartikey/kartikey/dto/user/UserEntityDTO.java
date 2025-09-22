package com.kartikey.kartikey.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String tlEmail;
    private String location;
    private String password;  // केवल add user में इस्तेमाल होगा
}
