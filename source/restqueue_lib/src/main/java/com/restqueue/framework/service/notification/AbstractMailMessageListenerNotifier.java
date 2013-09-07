package com.restqueue.framework.service.notification;

import com.restqueue.common.utils.StringUtils;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
/**
    * Copyright 2010-2013 Nik Tomkinson

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 * Date: 30/08/2013
 * Time: 17:59
 */
public class AbstractMailMessageListenerNotifier implements MessageListenerNotifier{
    protected static final Logger log = Logger.getLogger(AbstractMailMessageListenerNotifier.class);

    private String username;
    private String password;
    private String mailSmtpsHost;
    private int mailSmtpPort;
    private int mailSmtpSocketFactoryPort;
    private String subject;
    private String messageTemplate;

    public void notifyListener(MessageListenerAddress messageListenerAddress, String location, String eTag) {
        final String message = MessageFormat.format(messageTemplate, location, eTag);
        try {
            if(username==null || password==null){
                throw new MessagingException("username and password MUST be set before a notification email can be sent");
            }
            if(mailSmtpPort==0 || mailSmtpSocketFactoryPort==0 || mailSmtpsHost==null || StringUtils.isNullOrEmpty(subject) ||
                    StringUtils.isNullOrEmpty(messageTemplate)){
                throw new MessagingException("All of the following MUST be set before a notification email can be sent: mailSmtpsHost, mailSmtpPort, " +
                        "mailSmtpSocketFactoryPort, subject, messageTemplate");
            }

            send(messageListenerAddress.getReturnAddress().getAddress(), "", subject, message);
        }
        catch (MessagingException e) {
            log.error("Could not send email notification to " + messageListenerAddress.getReturnAddress().getAddress(), e);
        }
    }

    public void send(String recipientEmail, String ccEmail, String title, String message) throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host",mailSmtpsHost);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", String.valueOf(mailSmtpPort));
        props.setProperty("mail.smtp.socketFactory.port", String.valueOf(mailSmtpSocketFactoryPort));
        props.setProperty("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        final MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect(mailSmtpsHost, username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }

    public void setMailSmtpsHost(String mailSmtpsHost) {
        this.mailSmtpsHost = mailSmtpsHost;
    }

    public void setMailSmtpPort(int mailSmtpPort) {
        this.mailSmtpPort = mailSmtpPort;
    }

    public void setMailSmtpSocketFactoryPort(int mailSmtpSocketFactoryPort) {
        this.mailSmtpSocketFactoryPort = mailSmtpSocketFactoryPort;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
