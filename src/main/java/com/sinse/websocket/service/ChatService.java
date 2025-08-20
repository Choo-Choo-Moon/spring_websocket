package com.sinse.websocket.service;

import com.sinse.websocket.domain.Room;

import java.util.List;

public interface ChatService {
    List<Room> getRoomInfo();
    List<String> getMembers(String room);
}
