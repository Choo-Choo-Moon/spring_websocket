package com.sinse.websocket.service;

import com.sinse.websocket.domain.Member;

public interface MemberService {
    Member login(String id, String password);
}
