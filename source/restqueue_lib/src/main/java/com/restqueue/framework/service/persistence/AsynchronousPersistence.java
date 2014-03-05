package com.restqueue.framework.service.persistence;

import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import org.apache.log4j.Logger;

import java.util.*;

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
 * Date: 05/08/2013
 * Time: 10:05
 */
public class AsynchronousPersistence extends XmlFilePersistence implements Runnable{
    private volatile long lastUpdate=0L;
    private volatile int refreshInterval=30*1000;
    private volatile Set<Class> updatedChannels = new HashSet<Class>();
    private volatile boolean persistenceEnabled=true;
    private volatile XmlFilePersistence xmlFilePersistence = new XmlFilePersistence();
    private volatile Map<Class,List<EntryWrapper>> channelContentsMap = new HashMap<Class, List<EntryWrapper>>();
    private volatile Map<Class,ChannelState> channelStateMap = new HashMap<Class, ChannelState>();
    private volatile Map<Class,Object> registeredMessageListenersMap = new HashMap<Class, Object>();
    private volatile Map<Class,Object> messageListenerGroupRegistrationMap = new HashMap<Class, Object>();

    private static final Object LOCK = new Object();

    private static final Logger log = Logger.getLogger(AsynchronousPersistence.class);

    private static volatile AsynchronousPersistence instance;

    private AsynchronousPersistence() {
    }

    private final Object lock = new Object();

    @Override
    public void saveUpdated(final Class associatedChannelResourceClazz, Map<String, Object> changedState) {
        synchronized (lock){
            update(associatedChannelResourceClazz);
            this.registeredMessageListenersMap.put(associatedChannelResourceClazz, changedState.get(Persistence.MESSAGE_LISTENERS_KEY));
            this.messageListenerGroupRegistrationMap.put(associatedChannelResourceClazz, changedState.get(Persistence.MESSAGE_LISTENER_REGISTRATION_KEY));
            this.channelStateMap.put(associatedChannelResourceClazz, (ChannelState) changedState.get(Persistence.CHANNEL_STATE_KEY));
            this.channelContentsMap.put(associatedChannelResourceClazz, (List<EntryWrapper>) changedState.get(Persistence.CHANNEL_CONTENTS_KEY));
        }
    }

    private void update(Class associatedChannelResourceClazz) {
        updatedChannels.add(associatedChannelResourceClazz);
        lastUpdate=System.currentTimeMillis();
    }

    public void stopPersistence(){
        persistenceEnabled=false;
    }

    public void run() {
        while(persistenceEnabled){
            try {
                if(lastUpdate>(System.currentTimeMillis()-refreshInterval)){
                    log.info("Channel Changes detected - persisting");
                    lastUpdate=System.currentTimeMillis();

                    synchronized (lock) {
                        for (Class updatedChannel : updatedChannels) {
                            if (channelContentsMap.get(updatedChannel) != null) {
                                xmlFilePersistence.saveChannelContents(updatedChannel, channelContentsMap.get(updatedChannel));
                            }
                            if (channelStateMap.get(updatedChannel) != null) {
                                xmlFilePersistence.saveChannelState(updatedChannel, channelStateMap.get(updatedChannel));
                            }
                            if (registeredMessageListenersMap.get(updatedChannel) != null) {
                                xmlFilePersistence.saveMessageListeners(updatedChannel, registeredMessageListenersMap.get(updatedChannel));
                            }
                            if (messageListenerGroupRegistrationMap.get(updatedChannel) != null) {
                                xmlFilePersistence.saveMessageListenerRegistration(updatedChannel, messageListenerGroupRegistrationMap.get(updatedChannel));
                            }
                        }
                        updatedChannels.clear();
                    }
                }
                else{
                    log.info("No Channel Changes detected");
                }
                Thread.sleep(refreshInterval);
            }
            catch (InterruptedException e) {
                log.info("Polling Persistence Interrupted");
                persistenceEnabled=false;
            }
        }
    }

    public void setRefreshInterval(int refreshInterval) {
        if(refreshInterval>=5){
            this.refreshInterval = refreshInterval*1000;
        }
        else{
            throw new IllegalArgumentException("Cannot set persistence frequency to less than 5 seconds");
        }
    }

    public static AsynchronousPersistence getInstance(){
        if(instance == null){
            synchronized (LOCK){
                if(instance == null){
                    instance = new AsynchronousPersistence();
                }
            }
        }
        return instance;
    }
}
