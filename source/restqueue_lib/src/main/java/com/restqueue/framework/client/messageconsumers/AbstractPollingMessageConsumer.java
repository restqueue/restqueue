package com.restqueue.framework.client.messageconsumers;

import com.restqueue.framework.client.entrywrappers.EntrySummary;
import com.restqueue.framework.service.server.AbstractServer;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * This class holds the basic functionality to act as a message consumer that polls the specified channel for content
 * on a regular basis.
 * If the channel has contents, it calls the processMessages method (which must be implemented in a subclass of this
 * abstract class) to perform the custom operations on the messages.<BR/><BR/>
 *
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
    protected volatile String messageConsumerId;
    private volatile boolean pollingEnabled=false;
    private volatile long pollingFrequency =60;
    private volatile String channelEndpoint;
    private volatile String serverIpAddress = "localhost";
    private volatile Integer serverPort = AbstractServer.PORT;

    public void setMessageConsumerId(final String id){
        this.messageConsumerId=id;
    }

    /**
     * This sets the polling frequency but does not allow a frequency of more than one poll per second.
     * @param pollingFrequency integer number of seconds (i.e. 5 = once every five seconds)
     */
    public final void setPollingFrequencyInSeconds(long pollingFrequency) {
        //do not allow a polling period more frequent than one second
        if(pollingFrequency<1){
            this.pollingFrequency =1;
        }
        else{
            this.pollingFrequency = pollingFrequency;
        }
    }

    /**
     * Immediately stop polling
     */
    public void stopPolling(){
        pollingEnabled=false;
        this.interrupt();
    }

    /**
     * Start polling now
     */
    public void startPolling(){
        pollingEnabled=true;
        this.start();
    }

    /**
     * Stop polling once the current poll is complete
     */
    public void disablePolling(){
        pollingEnabled=false;
    }

    /**
     * Re-enable polling
     */
    public void enablePolling(){
        pollingEnabled=true;
    }

    /**
     * Poll in a separate thread and process the messages if there are any to process.
     */
    public void run(){
        basicMessageConsumer.setChannelEndpoint(channelEndpoint);
        basicMessageConsumer.setServerIpAddress(serverIpAddress);
        basicMessageConsumer.setServerPort(serverPort);

        String lastETag=null;

        while(pollingEnabled){
            basicMessageConsumer.setETag(lastETag);
            List<EntrySummary> allMessages = basicMessageConsumer.getAllMessages();
            if(basicMessageConsumer.haveContentsChanged() && !allMessages.isEmpty()){
                lastETag=basicMessageConsumer.getResponseETag();
                log.info("There are messages on channel "+channelEndpoint+" - processing!");
                processMessages(allMessages);
            }
            try {
                Thread.sleep(pollingFrequency *1000);
            } catch (InterruptedException e) {
                pollingEnabled=false;
            }
        }
    }

    /**
     * Implement this method to process the messages in exactly the way you need.
     * @param entrySummaries The messages in the channel.
     */
    public abstract void processMessages(List<EntrySummary> entrySummaries);

    /**
     * To set the channel endpoint to poll
     * @param channelEndpoint The channel endpoint (eg. http://{serverip}:{serverport}/channels/1.0/{channelName})
     */
    public void setChannelEndpoint(String channelEndpoint) {
        this.channelEndpoint = channelEndpoint;
    }

    /**
     * Set the server ip address where the channel is hosted. The default is localhost.
     * @param serverIpAddress The IP Address
     */
    public void setServerIpAddress(String serverIpAddress) {
        if(serverIpAddress!=null){
            this.serverIpAddress = serverIpAddress;
        }
    }

    /**
     * Set the server port where the channel is hosted. The default is 9998.
     * @param serverPort The port
     */
    public void setServerPort(Integer serverPort) {
        if(serverPort!=null){
            this.serverPort = serverPort;
        }
    }
}
