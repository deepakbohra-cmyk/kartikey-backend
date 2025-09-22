package com.kartikey.kartikey.service;

import com.kartikey.kartikey.dto.user.UserDTO;
import com.kartikey.kartikey.dto.user.UserEntityDTO;

import java.util.List;

public interface UserDataService {
    List<UserDTO> getAllUser();
    UserDTO addUser(UserEntityDTO userEntityDTO);
    UserDTO updateUser(Long id, UserEntityDTO userEntityDTO);  // नया method
    void deleteUser(Long id);
}
