package com.example.event_driven_practice.controller;

import com.example.event_driven_practice.domain.dto.NoticeResponse;
import com.example.event_driven_practice.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private final NoticeService  noticeService;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sse() {
        SseEmitter emitter = new SseEmitter();
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

}
