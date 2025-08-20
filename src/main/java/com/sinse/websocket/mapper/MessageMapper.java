package com.sinse.websocket.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinse.websocket.domain.message.*;
import com.sinse.websocket.dto.MessageDto;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * {@link MessageDto} command에 따라서 각기 다른 Message로 변환
     *
     * @param messageDto 메시지 Dto
     * @param <T> {@link BaseMessage}를 상속받은 메시지 객체
     * @return {@link BaseMessage}를 상속받은 메시지 객체
     * @throws JsonProcessingException
     */
    public <T extends BaseMessage> T toMessage(MessageDto messageDto) throws JsonProcessingException {
        switch (messageDto.getCommand()) {
            case "createRoom":
                return (T) objectMapper.convertValue(messageDto.getPayload(), RequestCreateRoomMessage.class);
            case "joinRoom":
                return (T) objectMapper.convertValue(messageDto.getPayload(), RequestJoinRoomMessage.class);
            case "leaveRoom":
                return (T) objectMapper.convertValue(messageDto.getPayload(), RequestLeaveRoomMessage.class);
            default:
                return (T) objectMapper.convertValue(messageDto.getPayload(), MemberMessage.class);
        }
    }

    /**
     * 문자열을 {@link MessageDto}로 변환하여 반환
     *
     * @param messageString 메시지 문자열
     * @return {@link MessageDto}
     * @throws JsonProcessingException
     */
    public MessageDto toMessageDto(String messageString) throws JsonProcessingException {
        return objectMapper.readValue(messageString, MessageDto.class);
    }

    /**
     * {@link SystemMessage}를 {@link MessageDto}로 변환하여 반환
     *
     * @param message 시스템 메시지
     * @return {@link MessageDto}
     */
    public MessageDto toMessageDto(SystemMessage message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setCommand(message.getCommand());
        return messageDto;
    }

    /**
     * {@link MemberMessage}를 {@link MessageDto}로 변환하여 반환
     *
     * @param message 멤버 메시지
     * @return {@link MessageDto}
     * @throws JsonProcessingException
     */
    public MessageDto toMessageDto(MemberMessage message) throws JsonProcessingException {
        MessageDto messageDto = new MessageDto();
        messageDto.setCommand("msg");
        messageDto.setPayload(message);
        return messageDto;
    }

    /**
     * {@link MessageDto}를 문자열로 변환하여 반환
     *
     * @param messageDto 메시지 DTO
     * @return {@link MessageDto}
     * @throws JsonProcessingException
     */
    public String toJsonString(MessageDto messageDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(messageDto);
    }
}
