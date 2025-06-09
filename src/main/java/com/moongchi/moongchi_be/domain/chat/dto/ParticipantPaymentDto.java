package com.moongchi.moongchi_be.domain.chat.dto;

import com.moongchi.moongchi_be.domain.chat.entity.PaymentStatus;
import com.moongchi.moongchi_be.domain.chat.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ParticipantPaymentDto {
    private Long userId;
    private String userName;
    private Role role;
    private PaymentStatus paymentStatus;
    private int patAmount;
}
