package com.moongchi.moongchi_be.domain.chat.dto;

import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@Builder
public class ParticipantDto {
    private Long participantId;
    private Long userId;
    private String nickname;
    private String profileUrl;
    private String role;
    private String payStatement;
    private boolean tradeCompleted;
    private int perPersonPrice;


    public static ParticipantDto from(Participant p) {
        return new ParticipantDto(
                p.getId(),
                p.getUser().getId(),
                p.getUser().getNickname(),
                p.getUser().getProfileUrl(),
                p.getRole() != null ? p.getRole().name() : null,
                p.getPaymentStatus() != null ? p.getPaymentStatus().name() : null,
                p.isTradeCompleted(),
                0
        );
    }

}
