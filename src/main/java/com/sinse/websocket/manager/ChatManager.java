package com.sinse.websocket.manager;

import com.sinse.websocket.domain.Member;
import com.sinse.websocket.domain.message.*;
import com.sinse.websocket.dto.MessageDto;
import com.sinse.websocket.mapper.MessageMapper;
import jakarta.websocket.Session;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ChatManager {
    private final MessageMapper mapper;
    private final String LOBBY_KEY;
    private final Map<String, Set<Session>> roomMap = new ConcurrentHashMap<>();
    private final Set<Session> memberMap = ConcurrentHashMap.newKeySet();

    public ChatManager(MessageMapper mapper) {
        this.mapper = mapper;
        LOBBY_KEY = "LOBBY_KEY" + UUID.randomUUID() + System.currentTimeMillis();
        roomMap.put(LOBBY_KEY, ConcurrentHashMap.newKeySet());
    }

    /**
     * 멤버세션을 멤버리스트에 등록하고 로비에 입장 시키기
     *
     * @param session 멤버 세션
     * @throws IOException
     */
    public void connection(Session session) throws IOException {
        memberMap.add(session);
        joinLobby(session);
    }

    /**
     * 연결이 끊어 질 때 멤버세션이 속한 방이 있는지 찾은 다음 방 이름을 찾았다면 해당 방과 로비에 연결 끊어짐을 알린다.
     *
     * @param session 멤버 세션
     * @throws IOException
     */
    public void disconnection(Session session) throws IOException {
        memberMap.remove(session);
        Optional<String> roomName = roomMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(session))
                .map(Map.Entry::getKey)
                .findFirst();
        if (roomName.isPresent()) {
            String name = roomName.get();
            Set<Session> roomSessions = roomMap.get(name);
            if (roomSessions != null) {
                roomSessions.remove(session);
                SystemMessage message = new SystemMessage("disconnected");
                broadcastSystemMessageToRoom(name, message);
                broadcastSystemMessageToLobby(message);
            }
        }
    }

    /**
     * 방 생성하기
     * {@link #roomMap} 방 목록에 같은 이름의 방이 없을 경우 방 생성
     *
     * @param message 방 생성 메시지
     */
    public synchronized void createRoom(RequestCreateRoomMessage message) throws IOException {
        if (roomMap.get(message.getRoomName()) == null) {
            roomMap.put(message.getRoomName(), ConcurrentHashMap.newKeySet());
            broadcastSystemMessageToLobby(new SystemMessage("createdRoom"));
        }

        if (message.getSession() != null) {
            roomMap.get(message.getRoomName()).add(message.getSession());
        }
    }

    /**
     * 로비에 존재하던 멤버 세션을 제거하고 방에 멤버 세션을 입장 시킨 뒤 방에 접속한 멤버들 세션에 방 입장 메시지 전달
     *
     * @param message 방 입장 메시지
     * @throws IOException
     */
    public void joinRoom(RequestJoinRoomMessage message) throws IOException {
        Set<Session> roomSessions = roomMap.get(message.getRoomName());
        if (roomSessions != null) {
            roomSessions.add(message.getSession());
            leaveLobby(message.getSession());
            broadcastSystemMessageToRoom(message.getRoomName(), new SystemMessage("joinedRoom"));
        }
    }

    /**
     * 멤버가 속한 방에서 멤버 세션을 제거한 뒤 방 멤버에게 방 나가기 메시지 전달 후 방을 나간 멤버를 로비에 입장 시킴
     *
     * @param message 방 퇴장 메시지
     * @throws IOException
     */
    public void leaveRoom(RequestLeaveRoomMessage message) throws IOException {
        Set<Session> roomSessions = roomMap.get(message.getRoomName());
        if (roomSessions != null) {
            roomSessions.remove(message.getSession());
            broadcastSystemMessageToRoom(message.getRoomName(), new SystemMessage("leavedRoom"));
            joinLobby(message.getSession());
        }
    }

    /**
     * 방 내부에서 주고 받는 채팅을 전달
     *
     * @param message 멤버 메시지
     * @throws IOException
     */
    public void onMessage(MemberMessage message) throws IOException {
        broadcastMemberMessageToRoom(message);
    }

    /**
     * 로비에 멤버 세션 추가
     *
     * @param session 멤버세션
     * @throws IOException
     */
    private void joinLobby(Session session) throws IOException {
        roomMap.get(LOBBY_KEY).add(session);
        broadcastSystemMessageToLobby(new SystemMessage("joinedLobby"));
    }

    /**
     * 로비에서 멤버 세션 제거
     *
     * @param session 멤버세션
     * @throws IOException
     */
    private void leaveLobby(Session session) throws IOException {
        roomMap.get(LOBBY_KEY).remove(session);
        broadcastSystemMessageToLobby(new SystemMessage("leavedLobby"));
    }

    /**
     * 로비에 접속되어있는 멤버세션에 시스템메시지를 전달
     *
     * @param message 시스템 메시지
     * @throws IOException
     */
    private void broadcastSystemMessageToLobby(SystemMessage message) throws IOException {
        Set<Session> lobbySessions = roomMap.get(LOBBY_KEY);
        if (lobbySessions == null) {
            return;
        }
        MessageDto messageDto = mapper.toMessageDto(message);
        broadcastMessage(messageDto, lobbySessions);
    }

    /**
     * 방에 접속되어있는 멤버세션에 시스템메시지를 전달
     *
     * @param roomName 방이름
     * @param message 시스템 메시지
     * @throws IOException
     */
    private void broadcastSystemMessageToRoom(String roomName, SystemMessage message) throws IOException {
        Set<Session> roomMembers = roomMap.get(roomName);
        if (roomMembers == null) {
            return;
        }
        MessageDto messageDto = mapper.toMessageDto(message);
        broadcastMessage(messageDto, roomMembers);
    }

    /**
     * 방에 접속되어있는 멤버세션에 멤버메세지 전달
     * @param message 멤버 메시지
     * @throws IOException
     */
    private void broadcastMemberMessageToRoom(MemberMessage message) throws IOException {
        Set<Session> roomMembers = roomMap.get(message.getRoomName());
        if (roomMembers == null) {
            return;
        }
        MessageDto messageDto = mapper.toMessageDto(message);
        broadcastMessage(messageDto, roomMembers);
    }

    private void broadcastMessage(MessageDto messageDto, Set<Session> sessions) throws IOException {
        String messageJson = mapper.toJsonString(messageDto);
        List<Session> sessionsCopy = new ArrayList<>(sessions);
        for (Session memberSession : sessionsCopy) {
            if (memberSession.isOpen()) {
                memberSession.getBasicRemote().sendText(messageJson);
            }
        }
    }

    public List<String> getMembers() {
        return roomMap.get(LOBBY_KEY).stream()
                .filter(Session::isOpen)
                .map(session -> ((Member) session.getUserProperties().get("member")).getId())
                .toList();
    }

    public List<String> getMembers(String room) {
        Set<Session> roomMembers = roomMap.get(room);
        if (roomMembers == null) {
            return List.of();
        }
        return memberMap.stream()
                .filter(roomMembers::contains)
                .map(session -> ((Member) session.getUserProperties().get("member")).getId())
                .collect(Collectors.toList());
    }

    public Map<String, Set<Session>> getRooms() {
        return roomMap.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(LOBBY_KEY))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

}
