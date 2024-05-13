package com.example.event_driven_practice.controller;

import com.example.event_driven_practice.domain.Member;
import com.example.event_driven_practice.domain.dto.MemberRequest;
import com.example.event_driven_practice.domain.dto.MemberResponse;
import com.example.event_driven_practice.service.MemberService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<?>memberList(){
        List<MemberResponse>list = memberService.memberList();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>memberDetail(@PathVariable("id")Long memberId){
        MemberResponse memberResponse = memberService.memberResponse(memberId);
        return ResponseEntity.ok().body(memberResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<?>memberCreate(@RequestBody MemberRequest memberRequest) throws MessagingException, UnsupportedEncodingException {
        Member member = memberService.memberCreate(memberRequest);
        return ResponseEntity.ok().body(member);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>memberDelete(@PathVariable("id")Long memberId){
        memberService.memberDelete(memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>memberUpdate(@RequestBody MemberRequest memberRequest,@PathVariable("id")Long memberId){
        Member member = memberService.memberUpdate(memberRequest,memberId);
        return ResponseEntity.ok().body(member);
    }

}
