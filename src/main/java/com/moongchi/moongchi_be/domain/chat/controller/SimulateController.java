package com.moongchi.moongchi_be.domain.chat.controller;

import com.moongchi.moongchi_be.domain.chat.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class SimulateController {
    private final ParticipantService participantService;

    @PostMapping("/{chatRoomId}/simulate-all")
    public ResponseEntity<String> simulateAll(@PathVariable Long chatRoomId) {
        participantService.simulateJoin(chatRoomId);
        participantService.simulatePayment(chatRoomId);
        participantService.simulatePurchase(chatRoomId);
        participantService.simulateTradeComplete(chatRoomId);
        return ResponseEntity.ok("전체 시뮬레이션 완료");
    }

}
