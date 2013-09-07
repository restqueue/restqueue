package com.restqueue.framework.service.notification;

import java.util.HashMap;
import java.util.Map;

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
 * Date: Dec 23, 2010
 * Time: 4:23:43 PM
 */
public final class MessageListenerNotificationRepository {
    private static final Object LOCK = new Object();
    private static Map<Class, MessageListenerNotification> messageListenerNotificationMap = new HashMap<Class, MessageListenerNotification>();

    public static MessageListenerNotification getOrCreateNotificationInstance(Class clazz) {
        if (messageListenerNotificationMap.get(clazz) == null) {
            synchronized (LOCK) {
                if (messageListenerNotificationMap.get(clazz) == null) {
                    messageListenerNotificationMap.put(clazz, MessageListenerNotification.createInstance(clazz));
                    messageListenerNotificationMap.get(clazz).load();
                }
            }
        }
        return messageListenerNotificationMap.get(clazz);
    }

}
