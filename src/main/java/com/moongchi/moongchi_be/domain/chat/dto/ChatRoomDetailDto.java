package com.moongchi.moongchi_be.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Setter @Getter
@AllArgsConstructor
@Builder
public class ChatRoomDetailDto {
    private Long id;
    private String title;
    private String status;
    private String imgUrl;
    private int price;
    private LocalDate deadline;
    private List<ParticipantDto> participants;
    private List<MessageDto> messages;
}
