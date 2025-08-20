package com.sinse.websocket.domain.message;

import jakarta.websocket.Session;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RequestCreateRoomMessage extends BaseMessage {
    private String roomName;
    private Session session;
}
