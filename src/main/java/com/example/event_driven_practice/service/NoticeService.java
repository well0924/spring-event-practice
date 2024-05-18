package com.example.event_driven_practice.service;

import com.example.event_driven_practice.domain.Member;
import com.example.event_driven_practice.domain.Notification;
import com.example.event_driven_practice.domain.NotificationType;
import com.example.event_driven_practice.domain.dto.NoticeResponse;
import com.example.event_driven_practice.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class NoticeService {

    private final NotificationRepository notificationRepository;

    //알림 저장
    @Transactional
    public void notifySave(Member member,NotificationType notificationType,String content, String data,String url){
        Notification notification = createNotification(member,notificationType,content,data,url);
        log.info("save::"+notification);
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
                .isRead(false)
                .data(data)
                .build();
    }
}
