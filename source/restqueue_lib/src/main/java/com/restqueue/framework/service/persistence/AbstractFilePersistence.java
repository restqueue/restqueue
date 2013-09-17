package com.restqueue.framework.service.persistence;

import com.restqueue.common.utils.FileUtils;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.notification.MessageListenerAddress;
import com.restqueue.framework.service.notification.MessageListenerGroup;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
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
 * Date: Jan 28, 2012
 * Time: 6:57:53 PM
 */
public abstract class AbstractFilePersistence implements Persistence {
    private static final Logger log = Logger.getLogger(AbstractFilePersistence.class);
    private PersistenceHelper persistenceHelper=new PersistenceHelper(getFilenameExtension(), getFilenameExtensionCode());

    public void saveUpdated(final Class associatedChannelResourceClazz, Map<String, Object> changedState) {
        //save just the changed items of persistable state
        if(changedState.containsKey(Persistence.MESSAGE_LISTENERS_KEY)){
            saveMessageListeners(associatedChannelResourceClazz, changedState.get(Persistence.MESSAGE_LISTENERS_KEY));
        }
        if(changedState.containsKey(Persistence.MESSAGE_LISTENER_REGISTRATION_KEY)){
            saveMessageListenerRegistration(associatedChannelResourceClazz, changedState.get(Persistence.MESSAGE_LISTENER_REGISTRATION_KEY));
        }
        if(changedState.containsKey(Persistence.CHANNEL_STATE_KEY)){
            saveChannelState(associatedChannelResourceClazz, (ChannelState) changedState.get(Persistence.CHANNEL_STATE_KEY));
        }
        if(changedState.containsKey(Persistence.CHANNEL_CONTENTS_KEY)){
            saveChannelContents(associatedChannelResourceClazz, (List<EntryWrapper>) changedState.get(Persistence.CHANNEL_CONTENTS_KEY));
        }
    }

    protected void saveMessageListeners(final Class associatedChannelResourceClazz, final Object registeredMessageListeners) {
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.MESSAGE_LISTENERS_BASE_FOLDER, associatedChannelResourceClazz, null);
        final boolean savedMessageListeners = FileUtils.saveToDisk(fullFilePath,
                persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz), new Serializer().toType(registeredMessageListeners, getFilenameExtensionCode()));
        if (savedMessageListeners) {
            log.info("MessageListener list saved to " + fullFilePath + persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else {
            log.info("MessageListener list could not be saved to " + fullFilePath + persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
    }

    protected abstract String getFilenameExtension();

    protected abstract String getFilenameExtensionCode();

    protected void saveMessageListenerRegistration(final Class associatedChannelResourceClazz, final Object messageListenerGroupRegistration) {
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER, associatedChannelResourceClazz, null);
        final boolean savedMessageListenerRegistration = FileUtils.saveToDisk(fullFilePath,
                persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz), new Serializer().toType(messageListenerGroupRegistration, getFilenameExtensionCode()));
        if(savedMessageListenerRegistration){
            log.info("MessageListener registration saved to "+fullFilePath+persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz) );
        }
        else{
            log.info("MessageListener registration could not be saved to "+fullFilePath+persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, MessageListenerAddress> loadMessageListeners(final Class associatedChannelResourceClazz) {
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.MESSAGE_LISTENERS_BASE_FOLDER, associatedChannelResourceClazz, null);
        final String restoredMessageListenersContents = FileUtils.restoreFromDisk(fullFilePath, persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if(restoredMessageListenersContents!=null){
            return (Map<String, MessageListenerAddress>)new Serializer().fromType(restoredMessageListenersContents, getFilenameExtensionCode());
        }
        return new HashMap<String, MessageListenerAddress>();
    }

    @SuppressWarnings("unchecked")
    public Map<String, MessageListenerGroup> loadMessageListenerRegistration(final Class associatedChannelResourceClazz) {
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER, associatedChannelResourceClazz, null);
        final String restoredMessageListenerRegistrationsContents = FileUtils.restoreFromDisk(fullFilePath,
                persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if(restoredMessageListenerRegistrationsContents!=null){
            return (Map<String, MessageListenerGroup>)new Serializer().fromType(restoredMessageListenerRegistrationsContents, getFilenameExtensionCode());
        }
        return new HashMap<String, MessageListenerGroup>();
    }

    @SuppressWarnings("unchecked")
    public ChannelState loadChannelState(final Class associatedChannelResourceClazz){
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.STATE_BASE_FOLDER, associatedChannelResourceClazz, null);
        final String restoredContents = FileUtils.restoreFromDisk(fullFilePath, persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if (restoredContents != null) {
            final ChannelState channelStateFromFile = ChannelState.fromMap((Map<String, Object>) new Serializer().fromType(restoredContents, getFilenameExtensionCode()));
            return ChannelState.defaultInstanceWithFieldsCopiedFrom(channelStateFromFile);
        }
        return new ChannelState();
    }

    protected void saveChannelState(final Class associatedChannelResourceClazz, final ChannelState channelState){
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.STATE_BASE_FOLDER, associatedChannelResourceClazz, null);
        persistenceHelper.saveChannelStateToPath(associatedChannelResourceClazz, channelState, fullFilePath);
    }

    protected void saveChannelContents(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents){
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.CONTENTS_BASE_FOLDER, associatedChannelResourceClazz, null);
        persistenceHelper.saveChannelContentsToPath(associatedChannelResourceClazz, channelContents, fullFilePath);
    }

    public List<EntryWrapper> loadChannelContents(final Class associatedChannelResourceClazz){
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.CONTENTS_BASE_FOLDER, associatedChannelResourceClazz, null);
        return persistenceHelper.loadChannelContentsFromPath(associatedChannelResourceClazz, fullFilePath);
    }
}
