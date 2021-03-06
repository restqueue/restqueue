package com.restqueue.framework.service.notification;

import com.restqueue.common.utils.DateUtils;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.service.backingstorefilters.*;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.persistence.*;
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
    private Snapshot snapshot;

    protected Map<String, MessageListenerAddress> registeredMessageListeners = new HashMap<String, MessageListenerAddress>();
    protected Map<String, MessageListenerGroup> messageListenerGroupsMapByUrl = new HashMap<String, MessageListenerGroup>();
    protected static Map<ReturnAddressType, MessageListenerNotifier> messageListenerNotifiers = new HashMap<ReturnAddressType, MessageListenerNotifier>();

    static{
        messageListenerNotifiers.put(ReturnAddressType.URL, new UrlMessageListenerNotifier());
        messageListenerNotifiers.put(ReturnAddressType.LOGFILE,new LoggingMessageListenerNotifier());
    }

    private MessageListenerNotification() {
        persistence = PersistenceProvider.getPersistenceImplementationBasedOnProgramArguments();
        snapshot = new SnapshotImpl();
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

        if(messageListenerGroupsMapByUrl.get(registrationUrl)==null){
            final MessageListenerGroup messageListenerGroup = new MessageListenerGroup();
            messageListenerGroup.setBackingStoreFilter(backingStoreFilter);
            messageListenerGroup.setFilterArguments(filterArguments);

            messageListenerGroupsMapByUrl.put(registrationUrl, messageListenerGroup);
        }

        messageListenerGroupsMapByUrl.get(registrationUrl).addListenerId(messageListenerAddress.getListenerId());
        save();
    }

    public void unRegisterMessageListener(final MessageListenerAddress messageListenerAddress, final String registrationUrl){
        if (messageListenerGroupsMapByUrl.get(registrationUrl) != null && !messageListenerGroupsMapByUrl.get(registrationUrl).getListenerIds().isEmpty()) {
            messageListenerGroupsMapByUrl.get(registrationUrl).removeListenerId(messageListenerAddress.getListenerId());

            if (messageListenerGroupsMapByUrl.get(registrationUrl).getListenerIds().isEmpty()) {
                messageListenerGroupsMapByUrl.remove(registrationUrl);
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
        else if(RegistrationPoint.UNRESERVED.equals(registrationPoint)){
            filter = new AllUnreservedFilter(new ArrivalOrderFilter());
        }
        return filter;
    }

    private void cleanUpRegisteredMessageListeners(){
        final Set<String> allRegisteredActiveMessageListeners = new HashSet<String>();
        for (MessageListenerGroup messageListenerGroup : messageListenerGroupsMapByUrl.values()) {
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

    public boolean notifyMessageListeners(final EntryWrapper entryWrapper, final ChannelState channelState){
        final Set<String> listenersToBeNotified = new HashSet<String>();

        //if the message has not expired, ignore
        if (entryWrapper.getDelayUntil() != null && !DateUtils.hasExpired(entryWrapper.getDelayUntil())) {
            return false;
        }

        //if the message is not the latest sequence, ignore
        if (entryWrapper.getSequence()>-1 && entryWrapper.getSequence()!=channelState.getNextMessageSequence()) {
            return false;
        }

        for (MessageListenerGroup messageListenerGroup : messageListenerGroupsMapByUrl.values()) {
            //if the message is of interest to the listener group - add members to notification list
            if(!messageListenerGroup.getBackingStoreFilter().filter(Collections.singletonList(entryWrapper), channelState, messageListenerGroup.getFilterArguments()).isEmpty()){
                //notification required
                listenersToBeNotified.addAll(messageListenerGroup.getListenerIds());
            }
        }

        //notify the interested parties
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
        return true;
    }

    private void save(){
        final HashMap<String, Object> changesMap = new HashMap<String, Object>();
        changesMap.put(Persistence.MESSAGE_LISTENERS_KEY, registeredMessageListeners);
        changesMap.put(Persistence.MESSAGE_LISTENER_REGISTRATION_KEY, messageListenerGroupsMapByUrl);
        persistence.saveUpdated(associatedChannelResourceClazz, changesMap);
    }

    protected void load() {
        registeredMessageListeners = persistence.loadMessageListeners(associatedChannelResourceClazz);
        messageListenerGroupsMapByUrl = persistence.loadMessageListenerRegistration(associatedChannelResourceClazz);
    }

    public void setMessageListenerNotifier(ReturnAddressType returnAddressType, MessageListenerNotifier messageListenerNotifier){
        if(returnAddressType!=null && messageListenerNotifier !=null){
            messageListenerNotifiers.put(returnAddressType, messageListenerNotifier);
        }
    }

    public Collection<MessageListenerAddress> getMessageListeners(final String registrationUrl) {
        if(messageListenerGroupsMapByUrl.get(registrationUrl)==null){
            return new ArrayList<MessageListenerAddress>();
        }

        final Set<String> listenerIds = messageListenerGroupsMapByUrl.get(registrationUrl).getListenerIds();

        final List<MessageListenerAddress> messageListenerAddresses = new ArrayList<MessageListenerAddress>();
        for (String listenerId : listenerIds) {
            messageListenerAddresses.add(registeredMessageListeners.get(listenerId));
        }

        return messageListenerAddresses;
    }

    public void takeSnapshot(String fileDateId) {
        snapshot.takeListenerSnapshot(associatedChannelResourceClazz, registeredMessageListeners, messageListenerGroupsMapByUrl, fileDateId);
    }

    public void restoreFromSnapshot(String snapshotId) {
        snapshot.overwriteCurrentListenerDataWithSnapshot(associatedChannelResourceClazz, snapshotId);

        //force a restore from working directory
        load();

        final String message = "Successfully restored listener data from snapshot";
        log.info(message);
    }

    public String getAssociatedChannelResourceClassName() {
        return associatedChannelResourceClazz.getName();
    }
}
