package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GroupPaymentResponseDto {
    private int totalAmount;
    private List<ParticipantPaymentDto> participants;
}
