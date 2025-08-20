package com.sinse.websocket.domain.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemberMessage extends BaseMessage{
    private String sender;
    private String roomName;
    private String message;
}
