package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.feedback.FeedBackDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackRequestDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackResponseDTO;
import com.kartikey.kartikey.dto.feedback.UpdateStatusRequest;
import com.kartikey.kartikey.service.FeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FeedBackController {

    private final FeedBackService feedBackService;

    @PostMapping
    public ResponseEntity<FeedBackResponseDTO> createFeedback(@RequestBody FeedBackRequestDTO dto) {
        return ResponseEntity.ok(feedBackService.createFeedback(dto));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<FeedBackDTO>> getMyFeedback(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(feedBackService.getAllFeedbackForUser(email));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<FeedBackDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(feedBackService.changeStatus(request.getStatus(), id));
    }

}
