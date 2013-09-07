package com.restqueue.framework.client.messageconsumers;

import com.restqueue.framework.service.entrywrappers.EntrySummary;
import com.restqueue.framework.service.server.AbstractServer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Date: Jan 26, 2011
 * Time: 9:52:28 PM
 */
public abstract class AbstractPollingMessageConsumer extends Thread {
    private static final Logger log = Logger.getLogger(AbstractPollingMessageConsumer.class);

    private BasicMessageConsumer basicMessageConsumer=new BasicMessageConsumer();
    private volatile String messageConsumerId;
    private volatile boolean pollingEnabled=false;
    private volatile long pollingPeriod=60;
    private volatile String channelEndpoint;
    private volatile String serverIpAddress = "localhost";
    private volatile Integer serverPort = AbstractServer.PORT;

    public void setMessageConsumerId(final String id){
        this.messageConsumerId=id;
    }

    public final void setPollingPeriodInSeconds(long pollingPeriod) {
        //do not allow a polling period more frequent than one second
        if(pollingPeriod<1){
            this.pollingPeriod=1;
        }
        else{
            this.pollingPeriod = pollingPeriod;
        }
    }

    public void stopPolling(){
        pollingEnabled=false;
        this.interrupt();
    }

    public void startPolling(){
        pollingEnabled=true;
        this.start();
    }

    public void disablePolling(){
        pollingEnabled=false;
    }

    public void enablePolling(){
        pollingEnabled=true;
    }

    public void run(){
        basicMessageConsumer.setChannelEndpoint(channelEndpoint);
        basicMessageConsumer.setServerIpAddress(serverIpAddress);
        basicMessageConsumer.setServerPort(serverPort);

        while(pollingEnabled){
            List<EntrySummary> allMessages = new ArrayList<EntrySummary>();
            allMessages = basicMessageConsumer.getAllMessages();
            processMessages(allMessages);
            try {
                Thread.sleep(pollingPeriod*1000);
            } catch (InterruptedException e) {
                pollingEnabled=false;
            }
        }
    }

    public abstract void processMessages(List<EntrySummary> entrySummaries);

    public void setChannelEndpoint(String channelEndpoint) {
        this.channelEndpoint = channelEndpoint;
    }

    public void setServerIpAddress(String serverIpAddress) {
        if(serverIpAddress!=null){
            this.serverIpAddress = serverIpAddress;
        }
    }

    public void setServerPort(Integer serverPort) {
        if(serverPort!=null){
            this.serverPort = serverPort;
        }
    }
}
