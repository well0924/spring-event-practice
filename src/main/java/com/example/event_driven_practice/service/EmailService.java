package com.example.event_driven_practice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String id;

    //@Async는 리턴타입이 Future 아니면 void로 해야 됨.
    @Async
    public void sendJoinMail(String userEmail) throws MessagingException, IOException {
        MimeMessage message = createMemberMessage(userEmail);
        log.info(message);
        try{
            javaMailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info(message.getMessageID());
        log.info("메시지 결과:::"+message.getContent().toString());
    }

    public MimeMessage createMemberMessage(String userEmail)throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : "+ userEmail);

        MimeMessage  message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, userEmail); // to 보내는 대상
        message.setSubject("회원 축하합니다."); //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">회원 가입 축하</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\">" +
                "<table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\">" +
                    "<tbody>" +
                        "<tr>" +
                            "<td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg +=              "회원가입 축하합니다. </td>" +
                        "</tr>" +
                    "</tbody>" +
                 "</table>" +
                "</div>";

        message.setText(msg, "utf-8", "html"); //내용, charset타입, subtype
        message.setFrom(new InternetAddress(id,"Admin")); //보내는 사람의 메일 주소, 보내는 사람 이름

        return message;
    }

}
