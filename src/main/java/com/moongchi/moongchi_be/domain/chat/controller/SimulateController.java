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

    @PostMapping("/{chatRoomId}/simulate-join")
    public ResponseEntity<String> simulateJoin(@PathVariable Long chatRoomId) {
        participantService.simulateJoin(chatRoomId);
        return ResponseEntity.ok("참여자 시뮬레이션 완료");
    }

    @PostMapping("/{chatRoomId}/simulate-payment")
    public ResponseEntity<String> simulatePayment(@PathVariable Long chatRoomId) {
        participantService.simulatePayment(chatRoomId);
        return ResponseEntity.ok("결제 시뮬레이션 완료");
    }

    @PostMapping("/{chatRoomId}/simulate-purchase")
    public ResponseEntity<String> simulatePurchase(@PathVariable Long chatRoomId) {
        participantService.simulatePurchase(chatRoomId);
        return ResponseEntity.ok("구매 시뮬레이션 완료");
    }

    @PostMapping("/{chatRoomId}/simulate-trade-complete")
    public ResponseEntity<String> simulateTradeComplete(@PathVariable Long chatRoomId) {
        participantService.simulateTradeComplete(chatRoomId);
        return ResponseEntity.ok("거래 완료 시뮬레이션 완료");
    }
}
