package com.restqueue.framework.service.notification;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * User: Nik Tomkinson

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.


 * Date: 03/11/2013
 * Time: 17:36
 */
public class AsynchronousNotification implements Runnable{
    private static final Logger log = Logger.getLogger(AsynchronousNotification.class);

    private static final Object LOCK = new Object();

    private volatile boolean notificationEnabled=true;
    private static final int REFRESH_INTERVAL = 5*1000; // 5 seconds

    private static AsynchronousNotification instance = new AsynchronousNotification();

    private final static ConcurrentMap<String, NotificationRequest> notificationRequestsMap = new ConcurrentHashMap<String, NotificationRequest>();

    private AsynchronousNotification() {
    }

    public void requestNotification(NotificationRequest notificationRequest){
        if(instance.notificationEnabled){
            synchronized (LOCK){
                notificationRequestsMap.put(makeKey(notificationRequest), notificationRequest);
            }
        }
    }

    public void run() {
        while (notificationEnabled) {
            final List<String> idsToRemove = new ArrayList<String>();

            synchronized (LOCK){
                for (NotificationRequest notificationRequest : notificationRequestsMap.values()) {
                    final boolean remove = notificationRequest.getMessageListenerNotification().notifyMessageListeners(notificationRequest.getEntryWrapper(), notificationRequest.getChannelState());
                    if (remove) {
                        idsToRemove.add(makeKey(notificationRequest));
                    }
                }

                for (String id : idsToRemove) {
                    notificationRequestsMap.remove(id);
                }
            }

            if(idsToRemove.size()>0){
                log.info("Notified Listeners concerning "+(idsToRemove.size())+" message(s)");
            }

            try {
                Thread.sleep(REFRESH_INTERVAL);
            }
            catch (InterruptedException e) {
                log.info("Notification Thread Interrupted");
                notificationEnabled=false;
            }
        }
    }

    public void stopNotification(){
        notificationEnabled=false;
    }

    public static AsynchronousNotification getInstance(){
        return instance;
    }

    private static String makeKey(NotificationRequest notificationRequest){
        return notificationRequest.getMessageListenerNotification().getAssociatedChannelResourceClassName()+"_"+
                notificationRequest.getEntryWrapper().getEntryId();
    }
}
