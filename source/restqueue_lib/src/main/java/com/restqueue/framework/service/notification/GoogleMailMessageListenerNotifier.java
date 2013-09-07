package com.restqueue.framework.service.notification;

import org.apache.log4j.Logger;
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
public class GoogleMailMessageListenerNotifier extends AbstractMailMessageListenerNotifier implements MessageListenerNotifier {
    protected static final Logger log = Logger.getLogger(GoogleMailMessageListenerNotifier.class);

    public GoogleMailMessageListenerNotifier(String username, String password) {
        setUsername(username);
        setPassword(password);
        setSubject("New channel message notification");
        setMessageTemplate("There is a new message at location:{0} with eTag:{1}");
        setMailSmtpPort(465);
        setMailSmtpsHost("smtp.gmail.com");
        setMailSmtpSocketFactoryPort(465);
    }
}
