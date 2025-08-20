package com.sinse.websocket.controller;

import com.sinse.websocket.exception.MemberNotFoundException;
import com.sinse.websocket.domain.Member;
import com.sinse.websocket.domain.Room;
import com.sinse.websocket.service.ChatService;
import com.sinse.websocket.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class MemberController {
    private final MemberService memberService;
    private final ChatService chatService;

    public MemberController(MemberService memberService, ChatService chatService) {
        this.memberService = memberService;
        this.chatService = chatService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/member/login";
    }

    @PostMapping("/login")
    public String requestLogin(
            @RequestParam("id") String id,
            @RequestParam("password") String password,
            HttpSession session,
            Model model
    ) {
        log.info("id {}", id);
        log.info("pass {}", password);

        try {
            Member member = memberService.login(id, password);
            if (member == null) {
                throw new MemberNotFoundException("로그인 실패");
            }
            List<Room> rooms = chatService.getRoomInfo();
            session.setAttribute("member", member);
            model.addAttribute("rooms", rooms);
            return "/index";
        } catch (Exception e) {
            throw new MemberNotFoundException(e);
        }
    }
}
