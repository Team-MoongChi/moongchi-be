package com.moongchi.moongchi_be.domain.chat.dto;

import com.moongchi.moongchi_be.domain.chat.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ParticipantPaymentDto {
    private Long userid;
    private String userName;
    private PaymentStatus paymentStatus;
    private int patAmount;
}
