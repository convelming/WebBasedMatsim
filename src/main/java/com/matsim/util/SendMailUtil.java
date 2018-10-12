package com.matsim.util;



import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by MingLU on 2018/8/2,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class SendMailUtil {



    public static void sendMail(String to,String emailMsg) throws Exception {

        Properties props = new Properties();
        props.setProperty("mail.debug", "true");
        props.setProperty("mail.smtp.auth", "true");

        // 协议名称设置为smtps，会使用SSL
        props.setProperty("mail.transport.protocol", "smtp");

        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        msg.setText(emailMsg);
        msg.setFrom(new InternetAddress("matsim@163.com"));
        msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(to));
        msg.setSubject("Password retrieve");
        msg.setText(emailMsg);

        Transport transport = session.getTransport();
        transport.connect("smtp.163.com", "matsim@163.com", "266835ming");

        transport.sendMessage( msg, new Address[]{new InternetAddress(to)});
        transport.close();
        System.out.println("Sent message successfully....");

    }

    public static void main(String[] args) throws Exception {
        SendMailUtil sendMailController = new SendMailUtil();
        sendMail( "convel@163.com","test" );
        System.out.println("done");
    }
}
