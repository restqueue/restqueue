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
 * Date: May 31, 2011
 * Time: 1:23:26 AM
 */
public class UrlMessageListenerNotifier implements MessageListenerNotifier {
    private static final Logger log = Logger.getLogger(UrlMessageListenerNotifier.class);

    public void notifyListener(MessageListenerAddress messageListenerAddress, final String location, final String eTag) {
        log.info("Unimplemented URL message listener triggered. Feel free to implement and inject in the Server onStart() method using: MessageListenerNotificationRepository.getOrCreateNotificationInstance({your_channel_resource_class}).setMessageListenerNotifier(ReturnAddressType.URL,{your_notifier_instance});");
    }
}
