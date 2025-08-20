package com.sinse.websocket.domain;

import lombok.Data;

import java.util.List;

@Data
public class Room {
    private String name;
    private List<String> members;
}
