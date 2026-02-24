package com.login_seguridad.login.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    final JavaMailSender mailSender;

    // @Value("${url_front}")
    @Value("${url}")
    private String urlBase;

    public MailService(JavaMailSender javaMailSender){
        this.mailSender = javaMailSender;
    }

    public void sendEmail(String emailUser, String token){
        String subject = "Verifique su cuenta";
        String url = urlBase+"/verifyEmail?token="+token;
        // String url = urlBase+"/verifyEmail/"+token;

        String body = "Haga click en el enlace para verificar su cuenta "+url;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailUser);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

}
