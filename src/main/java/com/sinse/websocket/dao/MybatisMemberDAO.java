package com.sinse.websocket.dao;

import com.sinse.websocket.exception.MemberNotFoundException;
import com.sinse.websocket.mapper.MemberMapper;
import com.sinse.websocket.domain.Member;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class MybatisMemberDAO implements MemberDAO {
    private final MemberMapper mapper;

    public MybatisMemberDAO(MemberMapper memberMapper) {
        this.mapper = memberMapper;
    }

    @Override
    public Member select(Member member) throws RuntimeException {
        try {
            return mapper.select(member);
        } catch (DataAccessException e) {
            throw new MemberNotFoundException(e);
        }
    }
}
