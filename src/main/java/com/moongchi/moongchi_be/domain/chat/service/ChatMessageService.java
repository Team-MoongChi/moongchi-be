package com.moongchi.moongchi_be.domain.chat.service;

import com.moongchi.moongchi_be.common.exception.custom.CustomException;
import com.moongchi.moongchi_be.common.exception.errorcode.ErrorCode;
import com.moongchi.moongchi_be.domain.chat.dto.ChatMessageRequestDto;
import com.moongchi.moongchi_be.domain.chat.dto.MessageDto;
import com.moongchi.moongchi_be.domain.chat.entity.ChatMessage;
import com.moongchi.moongchi_be.domain.chat.entity.MessageType;
import com.moongchi.moongchi_be.domain.chat.entity.Participant;
import com.moongchi.moongchi_be.domain.chat.repository.ChatMessageRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ChatRoomRepository;
import com.moongchi.moongchi_be.domain.chat.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository roomRepo;
    private final ParticipantRepository partRepo;
    private final ChatMessageRepository messageRepo;

    /** 메시지 전송 및 저장 */
    public MessageDto sendMessage(Long chatRoomId, Long userId, ChatMessageRequestDto req) {
        roomRepo.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Participant participant = partRepo.findByChatRoomIdAndUserId(chatRoomId,userId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND));

        ChatMessage saved = messageRepo.save(
                ChatMessage.builder()
                        .chatRoomId(chatRoomId)
                        .participantId(participant.getId())
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

    public ChatMessage save(Long chatRoomId, Long participantId, String message, String messageType) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoomId(chatRoomId);
        chatMessage.setParticipantId(participantId);
        chatMessage.setMessage(message);
        chatMessage.setMessageType(MessageType.TEXT);
        chatMessage.setSendAt(LocalDateTime.now());
        return messageRepo.save(chatMessage);
    }

    public MessageDto toDto(ChatMessage msg) {
        return new MessageDto(
                msg.getId(),
                msg.getParticipantId(),
                msg.getMessage(),
                msg.getMessageType().toString(),
                msg.getSendAt()
        );
    }

}
