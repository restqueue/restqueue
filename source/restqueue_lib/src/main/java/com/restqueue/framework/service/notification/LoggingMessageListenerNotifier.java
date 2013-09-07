package com.restqueue.framework.service.notification;

import com.restqueue.common.utils.FileUtils;
import com.restqueue.framework.service.persistence.Persistence;
import org.apache.log4j.Logger;

import java.io.*;

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
 * Date: 23/08/2013
 * Time: 15:56
 */
public class LoggingMessageListenerNotifier implements MessageListenerNotifier {
    protected static final Logger log = Logger.getLogger(LoggingMessageListenerNotifier.class);

    public void notifyListener(MessageListenerAddress messageListenerAddress, String location, String eTag) {
        final String filename = System.getProperty("user.home") + File.separator + Persistence.FRAMEWORK_NAME + File.separator +
                "notificationlog" + File.separator + messageListenerAddress.getReturnAddress().getAddress()+".csv";
        try {
            FileUtils.createOrAppendToFile(filename, "\""+location+"\",\""+eTag+"\"", true,"\"Message Url\",\"eTag\"");
        } catch (IOException e) {
            //just log it
            log.error("Exception when writing the message listener log file:" + filename, e);
        }
        log.info("New message notification successfully written to "+filename);
    }
}
