package com.example.event_driven_practice.repository;

import com.example.event_driven_practice.domain.Member;
import com.example.event_driven_practice.domain.Notification;
import com.example.event_driven_practice.domain.dto.NoticeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    //읽지 않은 알림 목록
    @Query(value = "select n from Notification n where n.isRead = false")
    List<NoticeResponse>noticeList();
}
