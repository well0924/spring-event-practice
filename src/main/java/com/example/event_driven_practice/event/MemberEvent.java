package com.example.event_driven_practice.event;

import com.example.event_driven_practice.domain.Member;
import lombok.Getter;

@Getter
public class MemberEvent {

    private final Member member;

    public MemberEvent(Member member){
        this.member = member;
    }
}
