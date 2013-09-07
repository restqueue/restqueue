package com.restqueue.framework.service.notification;

import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.service.backingstorefilters.ArrivalOrderFilter;
import com.restqueue.framework.service.backingstorefilters.BackingStoreFilter;
import com.restqueue.framework.service.backingstorefilters.BatchingFilter;
import com.restqueue.framework.service.backingstorefilters.SpecificPriorityFilter;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.persistence.Persistence;
import com.restqueue.framework.service.persistence.PersistenceProvider;
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
 * Date: May 31, 2011
 * Time: 1:02:21 AM
 */
public class MessageListenerNotification {
    private static final Logger log = Logger.getLogger(MessageListenerNotification.class);

    private Class associatedChannelResourceClazz;

    private Persistence persistence;

    protected Map<String, MessageListenerAddress> registeredMessageListeners = new HashMap<String, MessageListenerAddress>();
    protected Map<String, MessageListenerGroup> messageListenerGroupRegistration = new HashMap<String, MessageListenerGroup>();
    protected static Map<ReturnAddressType, MessageListenerNotifier> messageListenerNotifiers = new HashMap<ReturnAddressType, MessageListenerNotifier>();

    static{
        messageListenerNotifiers.put(ReturnAddressType.URL, new UrlMessageListenerNotifier());
        messageListenerNotifiers.put(ReturnAddressType.LOGFILE,new LoggingMessageListenerNotifier());
    }

    private MessageListenerNotification() {
        persistence = PersistenceProvider.getPersistenceImplementationBasedOnProgramArguments();
    }

    protected static MessageListenerNotification createInstance(Class associatedChannelResourceClazz){
        final MessageListenerNotification messageListenerNotification = new MessageListenerNotification();
        messageListenerNotification.associatedChannelResourceClazz = associatedChannelResourceClazz;
        return messageListenerNotification;
    }

    public void registerMessageListener(final MessageListenerAddress messageListenerAddress, final String registrationUrl,
                                        final RegistrationPoint registrationPoint, final Object[] filterArguments){
        registeredMessageListeners.put(messageListenerAddress.getListenerId(), messageListenerAddress);

        final BackingStoreFilter backingStoreFilter = createBackingStoreFilterFromRegistrationPoint(registrationPoint);

        if(messageListenerGroupRegistration.get(registrationUrl)==null){
            final MessageListenerGroup messageListenerGroup = new MessageListenerGroup();
            messageListenerGroup.setBackingStoreFilter(backingStoreFilter);
            messageListenerGroup.setFilterArguments(filterArguments);

            messageListenerGroupRegistration.put(registrationUrl, messageListenerGroup);
        }

        messageListenerGroupRegistration.get(registrationUrl).addListenerId(messageListenerAddress.getListenerId());
        save();
    }

    public void unRegisterMessageListener(final MessageListenerAddress messageListenerAddress, final String registrationUrl){
        if (messageListenerGroupRegistration.get(registrationUrl) != null && !messageListenerGroupRegistration.get(registrationUrl).getListenerIds().isEmpty()) {
            messageListenerGroupRegistration.get(registrationUrl).removeListenerId(messageListenerAddress.getListenerId());

            if (messageListenerGroupRegistration.get(registrationUrl).getListenerIds().isEmpty()) {
                messageListenerGroupRegistration.remove(registrationUrl);
            }
            cleanUpRegisteredMessageListeners();
            save();
        }
    }

    private BackingStoreFilter createBackingStoreFilterFromRegistrationPoint(RegistrationPoint registrationPoint) {
        BackingStoreFilter filter = new ArrivalOrderFilter();

        if(RegistrationPoint.SPECIFIC_BATCH.equals(registrationPoint)){
            filter = new BatchingFilter();
        }
        else if(RegistrationPoint.SPECIFIC_PRIORITY.equals(registrationPoint)){
            filter = new SpecificPriorityFilter();
        }
        return filter;
    }

    private void cleanUpRegisteredMessageListeners(){
        final Set<String> allRegisteredActiveMessageListeners = new HashSet<String>();
        for (MessageListenerGroup messageListenerGroup : messageListenerGroupRegistration.values()) {
            allRegisteredActiveMessageListeners.addAll(messageListenerGroup.getListenerIds());
        }

        final Set<String> allRegisteredInactiveMessageListeners = new HashSet<String>();

        for (MessageListenerAddress address : registeredMessageListeners.values()) {
            if(!allRegisteredActiveMessageListeners.contains(address.getListenerId())){
                allRegisteredInactiveMessageListeners.add(address.getListenerId());
            }
        }

        for (String registeredInactiveMessageListener : allRegisteredInactiveMessageListeners) {
            registeredMessageListeners.remove(registeredInactiveMessageListener);
        }
    }

    public void notifyMessageListeners(final EntryWrapper entryWrapper, final ChannelState channelState){
        final Set<String> listenersToBeNotified = new HashSet<String>();

        for (MessageListenerGroup messageListenerGroup : messageListenerGroupRegistration.values()) {
            if(!messageListenerGroup.getBackingStoreFilter().filter(Collections.singletonList(entryWrapper), channelState, messageListenerGroup.getFilterArguments()).isEmpty()){
                listenersToBeNotified.addAll(messageListenerGroup.getListenerIds());
            }
        }

        for (String listenerId : listenersToBeNotified) {
            final MessageListenerAddress listenerToNotify = registeredMessageListeners.get(listenerId);
            final MessageListenerNotifier messageListenerNotifier = messageListenerNotifiers.get(listenerToNotify.getReturnAddress().getType());

            if (messageListenerNotifier != null) {
                messageListenerNotifier.notifyListener(listenerToNotify, entryWrapper.getLinkUri(),
                        entryWrapper.getETag());
            }
            else{
                log.warn("There is no message notifier set up for:"+listenerToNotify.getReturnAddress().getType()+" for "+
                        associatedChannelResourceClazz+", please set the notifier implementation and retry.");
            }
        }
    }

    private void save(){
        final HashMap<String, Object> changesMap = new HashMap<String, Object>();
        changesMap.put(Persistence.MESSAGE_LISTENERS_KEY, registeredMessageListeners);
        changesMap.put(Persistence.MESSAGE_LISTENER_REGISTRATION_KEY, messageListenerGroupRegistration);
        persistence.saveUpdated(associatedChannelResourceClazz, changesMap);
    }

    protected void load() {
        registeredMessageListeners = persistence.loadMessageListeners(associatedChannelResourceClazz);
        messageListenerGroupRegistration = persistence.loadMessageListenerRegistration(associatedChannelResourceClazz);
    }

    public void setMessageListenerNotifier(ReturnAddressType returnAddressType, MessageListenerNotifier messageListenerNotifier){
        if(returnAddressType!=null && messageListenerNotifier !=null){
            messageListenerNotifiers.put(returnAddressType, messageListenerNotifier);
        }
    }

    public Collection<MessageListenerAddress> getMessageListeners(final String registrationUrl) {
        if(messageListenerGroupRegistration.get(registrationUrl)==null){
            return new ArrayList<MessageListenerAddress>();
        }

        final Set<String> listenerIds = messageListenerGroupRegistration.get(registrationUrl).getListenerIds();

        final List<MessageListenerAddress> messageListenerAddresses = new ArrayList<MessageListenerAddress>();
        for (String listenerId : listenerIds) {
            messageListenerAddresses.add(registeredMessageListeners.get(listenerId));
        }

        return messageListenerAddresses;
    }

    public void takeSnapshot(String fileDateId) {
        persistence.takeListenerSnapshot(associatedChannelResourceClazz, registeredMessageListeners, messageListenerGroupRegistration, fileDateId);
    }

    public void restoreFromSnapshot(String snapshotId) {
        persistence.overwriteCurrentListenerDataWithSnapshot(associatedChannelResourceClazz, snapshotId);

        //force a restore from working directory
        load();

        final String message = "Successfully restored listener data from snapshot";
        log.info(message);
    }
}
