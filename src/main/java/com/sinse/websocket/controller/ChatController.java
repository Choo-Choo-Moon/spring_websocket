package com.sinse.websocket.controller;

import com.sinse.websocket.exception.MemberNotFoundException;
import com.sinse.websocket.domain.Member;
import com.sinse.websocket.domain.Room;
import com.sinse.websocket.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {
    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping({"/members/{room}", "/members/"})
    @ResponseBody
    public List<String> getMembers(
            @PathVariable(required = false) String room
    ) {
        return chatService.getMembers(room);
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<Room> getRooms() {
        return chatService.getRoomInfo();
    }

    @GetMapping("/join/{roomName}")
    public String joinChatRoom(
            @PathVariable("roomName") String roomName, HttpSession session, Model model
    ) {
        if (session.getAttribute("member") instanceof Member) {
            return "redirect:/chat/"+roomName;
        } else {
            throw new MemberNotFoundException("멤버없음");
        }
    }

    @GetMapping("/chat/{roomName}")
    public String getChatPage(
            @PathVariable("roomName") String roomName, HttpSession session, Model model
    ) {
        if (session.getAttribute("member") instanceof Member) {
            model.addAttribute("roomName", roomName);
            return "/chat/index";
        } else {
            throw new MemberNotFoundException("멤버없음");
        }
    }
}
