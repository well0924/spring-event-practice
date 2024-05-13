package com.example.event_driven_practice.domain.dto;

import jdk.jfr.Name;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    private String userId;
    private String userName;
    private String userEmail;

}
