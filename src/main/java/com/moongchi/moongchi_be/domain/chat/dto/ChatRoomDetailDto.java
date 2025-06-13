package com.moongchi.moongchi_be.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Setter @Getter
@AllArgsConstructor
@Builder
@Schema(description = "채팅방 상세 정보 응답")
public class ChatRoomDetailDto {
    @Schema(description = "채팅방 ID")
    private Long id;
    @Schema(description = "채팅방 제목")
    private String title;
    @Schema(description = "채팅방 상태")
    private String status;
    @Schema(description = "대표 이미지")
    private String imgUrl;
    @Schema(description = "상품 총가격")
    private int price;
    @Schema(description = "마감일자")
    private LocalDate deadline;
    @Schema(description = "참여자 리스트")
    private List<ParticipantDto> participants;
    @Schema(description = "메시지 리스트")
    private List<MessageDto> messages;

}
