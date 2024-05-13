package com.example.event_driven_practice.service;

import com.example.event_driven_practice.domain.Member;
import com.example.event_driven_practice.domain.Notification;
import com.example.event_driven_practice.domain.NotificationType;
import com.example.event_driven_practice.domain.dto.NoticeResponse;
import com.example.event_driven_practice.event.MemberEvent;
import com.example.event_driven_practice.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class NoticeService {


    private final NotificationRepository notificationRepository;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    //회원 가입후 보낼 sse 알림
    public void onMemberCreatedNotify(MemberEvent event) {
        Member member = event.getMember();
        emitters.forEach(emitter -> {
            try {
                emitter.send(member);
                log.info("emitter:::"+emitter);
                //알림 내용 디비 저장.
                notifySave(member,NotificationType.MEMBER_JOIN,"회원가입 축하합니다.", member.getUserId(), "/api/member/create");
                log.info("member:::"+member);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        });
    }



    //알림 저장
    @Transactional
    public void notifySave(Member receiver,
                           NotificationType notificationType,
                           String content, String data,String url){
        Notification notification = createNotification(receiver,notificationType,content,data,url);
        notificationRepository.save(notification);
    }

    //알림 조회
    @Transactional(readOnly = true)
    public NoticeResponse noticeDetail(Long noticeId){
        Optional<Notification>notification = notificationRepository.findById(noticeId);
        return NoticeResponse
                .builder()
                .notification(notification.get())
                .build();
    }

    //알림 목록
    @Transactional
    public List<NoticeResponse>noticeList(){
        return notificationRepository.noticeList();
    }

    private Notification createNotification(Member member,
                                            NotificationType noticeType,
                                            String message,String data,String url){
        return Notification
                .builder()
                .receiver(member)
                .content(message)
                .notificationType(noticeType)
                .url(url)
                .isRead(true)
                .data(data)
                .build();
    }
}
