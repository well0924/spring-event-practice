package com.example.event_driven_practice.domain.dto;

import com.example.event_driven_practice.domain.Notification;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String content;
    private String url;
    private Boolean isRead;
    private String createdAt;

    @Builder
    public NoticeResponse(Notification notification) {
        this.id = notification.getId();
        this.content = String.valueOf(notification.getContent());
        this.url = String.valueOf(notification.getUrl());
        this.isRead = notification.getIsRead();
    }
}
