package com.sinse.websocket.service;


import com.sinse.websocket.ChatEndpoint;
import com.sinse.websocket.domain.Room;
import jakarta.websocket.Session;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatEndpoint chatEndpoint;

    public ChatServiceImpl(ChatEndpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
    }

    @Override
    public List<Room> getRoomInfo() {
        Map<String, Set<Session>> rooms = chatEndpoint.getRooms();
        return rooms.entrySet().stream()
                .map(this::convertRoom)
                .toList();
    }

    @Override
    public List<String> getMembers(String room) {
        if (room == null) {
            return chatEndpoint.getMembers();
        } else {
            return chatEndpoint.getMembers(room);
        }
    }

    private Room convertRoom(Map.Entry<String, Set<Session>> entry) {
        List<String> members = chatEndpoint.getMembers(entry.getKey());
        Room room = new Room();
        room.setName(entry.getKey());
        room.setMembers(members);
        return room;
    }
}
