package com.sinse.websocket.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SystemMessage extends BaseMessage {
    private String command;
}
