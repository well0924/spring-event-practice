package com.example.event_driven_practice.event;

import com.example.event_driven_practice.domain.Member;
import com.example.event_driven_practice.domain.NotificationType;
import com.example.event_driven_practice.service.EmailService;
import com.example.event_driven_practice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


@Log4j2
@RequiredArgsConstructor
@Component
public class MemberEventPublisher {

    private final EmailService emailService;

    private final NoticeService noticeService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,classes = {MemberEvent.class})
    public void publishEvent(MemberEvent memberEvent){
        log.info("event start");
        //이벤트 실행시 다음에 시작할 메서드 작동하기.
        log.info("email - send??");
        try{
            log.info("email sending");
            //이메일 로직 실행하기.
            emailService.sendJoinMail(memberEvent.getMember().getUserEmail());
        }catch (Exception e){
            //회원가입 및 이메일 전송에 실패한 경우에 대한 후처리.
            //메일 전송에 실패를 한 경우 트랜잭션 롤백
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info(e.getMessage());
        }
        log.info("event end");
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,classes = {MemberEvent.class})
    public void publishNotification(MemberEvent event){
        Member member = event.getMember();
        //회원 가입시 회원알림기능
        noticeService.notifySave(member, NotificationType.MEMBER_JOIN,"회원 가입을 축하합니다.", member.getUserId(), "/api/member/create");
    }

}
