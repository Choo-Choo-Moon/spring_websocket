package com.sinse.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sinse.websocket.config.HttpSessionConfiguration;
import com.sinse.websocket.domain.Member;
import com.sinse.websocket.domain.message.MemberMessage;
import com.sinse.websocket.domain.message.RequestCreateRoomMessage;
import com.sinse.websocket.domain.message.RequestJoinRoomMessage;
import com.sinse.websocket.domain.message.RequestLeaveRoomMessage;
import com.sinse.websocket.dto.MessageDto;
import com.sinse.websocket.manager.ChatManager;
import com.sinse.websocket.mapper.MessageMapper;
import com.sinse.websocket.util.ApplicationContextProvider;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@ServerEndpoint(value = "/ws/multi", configurator = HttpSessionConfiguration.class)
@Component
@DependsOn("applicationContextProvider")
public class ChatEndpoint {
    private final ChatManager chatManager;
    private final MessageMapper mapper;

    public ChatEndpoint() {
        ApplicationContext context = ApplicationContextProvider.getContext();
        chatManager = context.getBean(ChatManager.class);
        mapper = context.getBean(MessageMapper.class);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if (httpSession == null) {
            return;
        }
        Member member = (Member) httpSession.getAttribute("member");
        if (member != null) {
            session.getUserProperties().put("member", member);
            chatManager.connection(session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            MessageDto messageDto = mapper.toMessageDto(message);
            switch (messageDto.getCommand()) {
                case "createRoom": {
                    RequestCreateRoomMessage createRoomMessage = mapper.toMessage(messageDto);
                    chatManager.createRoom(createRoomMessage);
                }
                break;
                case "leaveRoom": {
                    RequestLeaveRoomMessage leaveRoomMessage = mapper.toMessage(messageDto);
                    leaveRoomMessage.setSession(session);
                    chatManager.leaveRoom(leaveRoomMessage);
                }
                break;
                case "joinRoom": {
                    RequestJoinRoomMessage joinRoomMessage = mapper.toMessage(messageDto);
                    joinRoomMessage.setSession(session);
                    chatManager.joinRoom(joinRoomMessage);
                }
                break;
                case "msg": {
                    MemberMessage memberMessage = mapper.toMessage(messageDto);
                    chatManager.onMessage(memberMessage);
                }
                break;
                default:
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON message: {}", message, e);
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        if (session != null) {
            chatManager.disconnection(session);
            if (session.isOpen()) {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye"));
            }
        }
    }

    public List<String> getMembers() {
        return chatManager.getMembers();
    }

    public List<String> getMembers(String room) {
        return chatManager.getMembers(room);
    }

    public Map<String, Set<Session>> getRooms() {
        return chatManager.getRooms();
    }
}
