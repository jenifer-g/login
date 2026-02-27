package com.login_seguridad.login.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    final JavaMailSender mailSender;

    @Value("${url_front}")
    private String urlFront;
    @Value("${url}")
    private String urlBase;

    public MailService(JavaMailSender javaMailSender){
        this.mailSender = javaMailSender;
    }

    public void sendEmail(String emailUser, String token, String message){
        String subject = "Verifique su cuenta";
        // String url = urlBase+"/verifyEmail?token="+token;
        String url = urlFront+"/verifyEmail?token="+token;

        String body = message+" "+url;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(emailUser);
        msg.setSubject(subject);
        msg.setText(body);

        mailSender.send(msg);
    }
    public void recoverPasswordEmail(String emailUser, String token){
        String subject = "Restablecer contraseña";
        // String url = urlBase+"/recoverPassword?token="+token;
        String url = urlFront+"/recoverPassword?token="+token;

        String body = "Haga click en el enlace para recuperar su contraseña "+url;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailUser);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }



}
