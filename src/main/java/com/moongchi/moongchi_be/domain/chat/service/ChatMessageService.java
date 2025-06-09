package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
import com.moongchi.moongchi_be.domain.chat.repository.ChatMessageRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository messageRepo;

    /** 메시지 전송 및 저장 */
    public MessageDto sendMessage(Long chatRoomId, ChatMessageRequestDto req) {
        roomRepo.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        ChatMessage saved = messageRepo.save(
                ChatMessage.builder()
                        .chatRoomId(chatRoomId.toString())
                        .participantId(req.getParticipantId())
                        .message(req.getMessage())
                        .build()
        );

        return new MessageDto(
                saved.getId(),
                saved.getParticipantId(),
                saved.getMessage(),
                saved.getMessageType().name(),
                saved.getSendAt()
        );
    }

    /** 채팅방 내 모든 메시지 조회 */
    public List<MessageDto> getMessages(Long chatRoomId) {
        roomRepo.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return messageRepo.findByChatRoomIdOrderBySendAtAsc(chatRoomId.toString())
                .stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getParticipantId(),
                        m.getMessage(),
                        m.getMessageType().name(),
                        m.getSendAt()
                ))
                .collect(Collectors.toList());
    }

}
