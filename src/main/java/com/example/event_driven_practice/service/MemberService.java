package com.example.event_driven_practice.service;

import com.example.event_driven_practice.domain.Member;
import com.example.event_driven_practice.domain.dto.MemberRequest;
import com.example.event_driven_practice.domain.dto.MemberResponse;
import com.example.event_driven_practice.event.MemberEvent;
import com.example.event_driven_practice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public List<MemberResponse>memberList(){
        List<Member>list = memberRepository.findAll();

        if(list.isEmpty()){
            throw new RuntimeException("회원 목록이 없습니다.");
        }

        return list.stream().map(MemberResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberResponse memberResponse(Long memberId){
        Optional<Member>member = memberRepository.findById(memberId);
        return MemberResponse
                .builder()
                .member(member.get())
                .build();
    }

    //회원 가입
    @Transactional
    public Member memberCreate(MemberRequest memberRequest) {
        Member member = Member
                .builder()
                .userId(memberRequest.getUserId())
                .userName(memberRequest.getUserName())
                .userEmail(memberRequest.getUserEmail())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        //1.회원 가입
        memberRepository.save(member);
        //2.회원 관련 이벤트 실행 -> 이메일 발송 /알림 기능
        applicationEventPublisher.publishEvent(new MemberEvent(member));
        return member;
    }

    @Transactional
    public Member memberUpdate(MemberRequest memberRequest,Long memberId){
        Optional<Member>member = memberRepository.findById(memberId);

        member.get().memberUpdate(memberRequest);

        return member.get();
    }

    @Transactional
    public void memberDelete(Long memberId){
        memberRepository.deleteById(memberId);
    }
}
