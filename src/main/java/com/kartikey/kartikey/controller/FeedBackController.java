package com.kartikey.kartikey.controller;

import com.kartikey.kartikey.dto.feedback.FeedBackRequestDTO;
import com.kartikey.kartikey.dto.feedback.FeedBackResponseDTO;
import com.kartikey.kartikey.service.FeedBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
