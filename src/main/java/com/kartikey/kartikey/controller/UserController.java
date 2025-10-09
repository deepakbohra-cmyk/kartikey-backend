package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.user.ChangePasswordRequest;
import com.kartikey.kartikey.dto.user.ResetPasswordRequest;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userDataService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userDataService.changePassword(request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userDataService.resetPassword(request.getEmail());
            return ResponseEntity.ok(Map.of(
                    "message", "Password has been reset successfully to default",
                    "newPassword", "#*pass12*#"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}
