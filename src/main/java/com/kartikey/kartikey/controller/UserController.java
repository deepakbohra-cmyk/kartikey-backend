package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.user.UserDTO;
import com.kartikey.kartikey.dto.user.UserEntityDTO;
import com.kartikey.kartikey.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserDataService userDataService;

    @GetMapping("/alluser")
    public ResponseEntity<List<UserDTO>> getAllUser() {
        List<UserDTO> users = userDataService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/add")
    public ResponseEntity<?> register(@RequestBody UserEntityDTO userEntityDTO) {
        try {
            UserDTO saved = userDataService.addUser(userEntityDTO);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody UserEntityDTO userDTO) {
        try {
            UserDTO updated = userDataService.updateUser(id, userDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userDataService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
