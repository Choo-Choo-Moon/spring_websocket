package com.sinse.websocket.mapper;

import com.sinse.websocket.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    Member select(Member member);
}
