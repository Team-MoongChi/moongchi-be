package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.domain.chat.dto.ChatRoomResponse;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoom;
import com.moongchi.moongchi_be.domain.chat.entity.ChatRoomStatus;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    public List<ChatRoomResponse> getAllChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream()
                .map(ChatRoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateChatRoomStatus(Long chatRoomId, ChatRoomStatus status) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));

        chatRoom.setStatus(status);
        chatRoomRepository.save(chatRoom);

        String systemMessage = getSystemMessageForStatus(status);

        chatMessageService.sendSystemMessage(
                chatRoom.getId().toString(),
                systemMessage
        );
    }

    private String getSystemMessageForStatus(ChatRoomStatus status) {
        return switch (status) {
            case RECRUITING -> "공구가 시작 되었습니다. 참여자를 모집 중입니다.";
            case RECRUITED -> "모집이 완료 되었습니다. 각 참여자들은 결제를 진행해주세요.";
            case PAYING -> "결제가 시작되었습니다. n분의 1 결제를 완료해주세요.";
            case PURCHASED -> "물품 구매가 완료되었습니다. 오프라인 만남을 진행해주세요.";
            case COMPLETED -> "공구가 완료되었습니다. 리뷰를 남겨주세요.";
        };
    }



}
