package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {
    private final ParticipantService participantService;

    @PostMapping("/{participantId}/pay")
    public ResponseEntity<Void> pay(@PathVariable Long participantId) {
        participantService.pay(participantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{participantId}/trade-complete")
    public ResponseEntity<Void> tradeComplete(@PathVariable Long participantId) {
        participantService.tradeComplete(participantId);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/{participantId}/review")
//    public ResponseEntity<Review> review(
//            @PathVariable Long participantId,
//            @RequestBody ReviewDto dto) {
//        Review saved = participantService.review(participantId, dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
//    }
}
