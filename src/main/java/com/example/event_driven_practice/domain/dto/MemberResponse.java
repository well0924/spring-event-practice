package com.example.event_driven_practice.domain.dto;

import com.example.event_driven_practice.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;

    private String userId;

    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    public MemberResponse(Member member){
        this.id = member.getId();
        this.userId = member.getUserId();
        this.userName = member.getUserName();
        this.createdTime = member.getCreatedTime();
        this.updatedTime = member.getUpdatedTime();
    }
}
