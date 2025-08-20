package com.sinse.websocket.service;

import com.sinse.websocket.dao.MemberDAO;
import com.sinse.websocket.domain.Member;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {
   private final MemberDAO memberDAO;

   public MemberServiceImpl(MemberDAO memberDAO) {
       this.memberDAO = memberDAO;
   }

    @Override
    public Member login(String id, String password) {
        Member member = new Member();
        member.setId(id);
        member.setPassword(password);
        return memberDAO.select(member);
    }
}
