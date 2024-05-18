package com.example.event_driven_practice.controller;

import com.example.event_driven_practice.domain.Member;
import com.example.event_driven_practice.domain.NotificationType;
import com.example.event_driven_practice.domain.dto.NoticeResponse;
import com.example.event_driven_practice.event.MemberEvent;
import com.example.event_driven_practice.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@RestController
@AllArgsConstructor
public class NotifyController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NoticeService  noticeService;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse() {
        //연결 지속 시간 설정
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        log.info("emitter:::"+emitter);
        return emitter;
    }

    @GetMapping("/list")
    public ResponseEntity<?>noticeList(){
        List<NoticeResponse>list = noticeService.noticeList();
        return ResponseEntity
                .ok()
                .body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse>noticeDetail(@PathVariable("id") Long noticeId){
        NoticeResponse noticeResponse = noticeService.noticeDetail(noticeId);
        return ResponseEntity.ok().body(noticeResponse);
    }


    //회원 가입후 보낼 sse 알림
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener(MemberEvent.class)
    public void onMemberCreatedNotify(MemberEvent event) {
        Member member =  event.getMember();
        log.info(member);
        emitters.forEach(emitter -> {
            try {
                emitter.send("회원가입을 축하합니다.");
                log.info("emitter???:::"+emitter);
                //알림 내용 디비 저장.
                noticeService.notifySave(member, NotificationType.MEMBER_JOIN,"회원가입 축하합니다.",member.getUserId(), "/api/member/create");
                log.info(member);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        });
    }
}
