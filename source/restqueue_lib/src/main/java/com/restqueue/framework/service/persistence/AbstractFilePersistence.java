package com.restqueue.framework.service.persistence;

import com.restqueue.common.utils.FileUtils;
import com.restqueue.common.utils.StringUtils;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.exception.ChannelStoreException;
import com.restqueue.framework.service.notification.MessageListenerAddress;
import com.restqueue.framework.service.notification.MessageListenerGroup;
import com.restqueue.framework.service.server.AbstractServer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
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
 * Date: Jan 28, 2012
 * Time: 6:57:53 PM
 */
public abstract class AbstractFilePersistence implements Persistence {
    private static final Logger log = Logger.getLogger(AbstractFilePersistence.class);

    private static final String SNAPSHOT_ID_PLACEHOLDER = "yyyyMMddHHmmss";

    private static final String CHANNEL_NAME_PLACEHOLDER = "$CHANNEL_NAME$";

    private static final String SERVER_ID = "server_"+ AbstractServer.PORT;

    private static final String BASE_FOLDER = System.getProperty("user.home") +
            File.separator +
            FRAMEWORK_NAME +
            File.separator +
            SERVER_ID +
            File.separator;

    private static final String CONTENTS_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            "contents" +
            File.separator;

    private static final String STATE_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            "state" +
            File.separator;

    private static final String MESSAGE_LISTENERS_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            Persistence.MESSAGE_LISTENERS_KEY +
            File.separator;

    private static final String MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            Persistence.MESSAGE_LISTENER_REGISTRATION_KEY +
            File.separator;

    private static final String SNAPSHOT_BASE_FOLDER = BASE_FOLDER+
            CHANNEL_NAME_PLACEHOLDER +
            File.separator+
            "snapshots" +
            File.separator;

    private static final String CONTENTS_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "contents" +
            File.separator;

    private static final String STATE_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "state" +
            File.separator;

    private static final String LISTENERS_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "messagelisteners" +
            File.separator;

    private static final String LISTENER_REGISTRATION_SNAPSHOT_FOLDER = SNAPSHOT_BASE_FOLDER +
            SNAPSHOT_ID_PLACEHOLDER +
            File.separator +
            "messagelistenerregistration" +
            File.separator;


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
        final String fullFilePath = fillOutPath(MESSAGE_LISTENERS_BASE_FOLDER, associatedChannelResourceClazz, null);
        final boolean savedMessageListeners = FileUtils.saveToDisk(fullFilePath,
                classBasedFileNameWithExtension(associatedChannelResourceClazz), new Serializer().toType(registeredMessageListeners, getFilenameExtensionCode()));
        if (savedMessageListeners) {
            log.info("MessageListener list saved to " + fullFilePath + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else {
            log.info("MessageListener list could not be saved to " + fullFilePath + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
    }

    protected abstract String getFilenameExtension();

    protected abstract String getFilenameExtensionCode();

    protected void saveMessageListenerRegistration(final Class associatedChannelResourceClazz, final Object messageListenerGroupRegistration) {
        final String fullFilePath = fillOutPath(MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER, associatedChannelResourceClazz, null);
        final boolean savedMessageListenerRegistration = FileUtils.saveToDisk(fullFilePath,
                classBasedFileNameWithExtension(associatedChannelResourceClazz), new Serializer().toType(messageListenerGroupRegistration, getFilenameExtensionCode()));
        if(savedMessageListenerRegistration){
            log.info("MessageListener registration saved to "+fullFilePath+classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("MessageListener registration could not be saved to "+fullFilePath+classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, MessageListenerAddress> loadMessageListeners(final Class associatedChannelResourceClazz) {
        final String fullFilePath = fillOutPath(MESSAGE_LISTENERS_BASE_FOLDER, associatedChannelResourceClazz, null);
        final String restoredMessageListenersContents = FileUtils.restoreFromDisk(fullFilePath, classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if(restoredMessageListenersContents!=null){
            return (Map<String, MessageListenerAddress>)new Serializer().fromType(restoredMessageListenersContents, getFilenameExtensionCode());
        }
        return new HashMap<String, MessageListenerAddress>();
    }

    @SuppressWarnings("unchecked")
    public Map<String, MessageListenerGroup> loadMessageListenerRegistration(final Class associatedChannelResourceClazz) {
        final String fullFilePath = fillOutPath(MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER, associatedChannelResourceClazz, null);
        final String restoredMessageListenerRegistrationsContents = FileUtils.restoreFromDisk(fullFilePath,
                classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if(restoredMessageListenerRegistrationsContents!=null){
            return (Map<String, MessageListenerGroup>)new Serializer().fromType(restoredMessageListenerRegistrationsContents, getFilenameExtensionCode());
        }
        return new HashMap<String, MessageListenerGroup>();
    }

    @SuppressWarnings("unchecked")
    public ChannelState loadChannelState(final Class associatedChannelResourceClazz){
        final String fullFilePath = fillOutPath(STATE_BASE_FOLDER, associatedChannelResourceClazz, null);
        final String restoredContents = FileUtils.restoreFromDisk(fullFilePath, classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if (restoredContents != null) {
            final ChannelState channelStateFromFile = ChannelState.fromMap((Map<String, Object>) new Serializer().fromType(restoredContents, getFilenameExtensionCode()));
            return ChannelState.defaultInstanceWithFieldsCopiedFrom(channelStateFromFile);
        }
        return new ChannelState();
    }

    protected void saveChannelState(final Class associatedChannelResourceClazz, final ChannelState channelState){
        final String fullFilePath = fillOutPath(STATE_BASE_FOLDER, associatedChannelResourceClazz, null);
        saveChannelStateToPath(associatedChannelResourceClazz, channelState, fullFilePath);
    }

    protected void takeChannelStateSnapshot(final Class associatedChannelResourceClazz, final ChannelState channelState, final String snapshotId){
        final String fullFilePath = fillOutPath(STATE_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        saveChannelStateToPath(associatedChannelResourceClazz, channelState, fullFilePath);
    }

    protected void takeChannelMessageListenerSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){

    }

    protected void takeChannelMessageListenerRegistrationSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){

    }

    private void saveChannelStateToPath(final Class associatedChannelResourceClazz, final ChannelState channelState, final String pathToUse){
        final String fileName = classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(pathToUse, fileName, channelState.serializeToType(getFilenameExtensionCode()))){
            log.info("Channel state for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    pathToUse + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel state has not been saved.");
        }
    }

    protected void saveChannelContents(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents){
        final String fullFilePath = fillOutPath(CONTENTS_BASE_FOLDER, associatedChannelResourceClazz, null);
        saveChannelContentsToPath(associatedChannelResourceClazz, channelContents, fullFilePath);
    }

    protected void takeChannelContentsSnapshot(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents, final String snapshotId) {
        final String fullFilePath = fillOutPath(CONTENTS_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        saveChannelContentsToPath(associatedChannelResourceClazz, channelContents,fullFilePath);
    }

    private void saveChannelContentsToPath(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents, final String pathToUse){
        final String fileName = classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(pathToUse, fileName, new Serializer().toType(channelContents, getFilenameExtensionCode()))){
            log.info("Channel contents for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    pathToUse + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel contents have not been saved.");
        }
    }

    public List<EntryWrapper> loadChannelContents(final Class associatedChannelResourceClazz){
        final String fullFilePath = fillOutPath(CONTENTS_BASE_FOLDER, associatedChannelResourceClazz, null);
        return loadChannelContentsFromPath(associatedChannelResourceClazz, fullFilePath);
    }

    @SuppressWarnings("unchecked")
    private List<EntryWrapper> loadChannelContentsFromPath(final Class associatedChannelResourceClazz, String pathToUse){
            final List<EntryWrapper> channelContents = new ArrayList<EntryWrapper>();
        final String restoredContents = FileUtils.restoreFromDisk(pathToUse, classBasedFileNameWithExtension(associatedChannelResourceClazz));
        if (restoredContents != null) {
            channelContents.addAll((List<EntryWrapper>) new Serializer().fromType(restoredContents, getFilenameExtensionCode()));
        }

        return channelContents;
    }

    public List<EntryWrapper> loadChannelContentsSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){
        final String fullFilePath = fillOutPath(CONTENTS_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        return loadChannelContentsFromPath(associatedChannelResourceClazz,fullFilePath);
    }

    public void overwriteCurrentDataWithSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){
        //get specified files
        final String snapshotContentsFileName = fillOutPath(CONTENTS_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                classBasedFileNameWithExtension(associatedChannelResourceClazz);
        final String snapshotStateFileName = fillOutPath(STATE_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                classBasedFileNameWithExtension(associatedChannelResourceClazz);

        //copy specified file into working directory
        try {
            FileUtils.copyFile(snapshotContentsFileName, fillOutPath(CONTENTS_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    classBasedFileNameWithExtension(associatedChannelResourceClazz));
            FileUtils.copyFile(snapshotStateFileName, fillOutPath(STATE_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    classBasedFileNameWithExtension(associatedChannelResourceClazz));

        } catch (IOException e) {
            log.error("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage());
            throw new ChannelStoreException("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage(),
                    ChannelStoreException.ExceptionType.FILE_SYSTEM);
        }
    }

    public void overwriteCurrentListenerDataWithSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){
        //get specified files
        final String snapshotListenersFileName = fillOutPath(LISTENERS_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                classBasedFileNameWithExtension(associatedChannelResourceClazz);
        final String snapshotListenerRegistrationFileName = fillOutPath(LISTENER_REGISTRATION_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                classBasedFileNameWithExtension(associatedChannelResourceClazz);

        //copy specified file into working directory
        try {
            FileUtils.copyFile(snapshotListenersFileName, fillOutPath(MESSAGE_LISTENERS_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    classBasedFileNameWithExtension(associatedChannelResourceClazz));
            FileUtils.copyFile(snapshotListenerRegistrationFileName, fillOutPath(MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    classBasedFileNameWithExtension(associatedChannelResourceClazz));

        } catch (IOException e) {
            log.error("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage());
            throw new ChannelStoreException("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage(),
                    ChannelStoreException.ExceptionType.FILE_SYSTEM);
        }
    }

    public List<String> getSnapshotList(final Class associatedChannelResourceClazz) {
        final File snapshotsFile = new File(fillOutPath(SNAPSHOT_BASE_FOLDER, associatedChannelResourceClazz, null));

        final ArrayList<String> links = new ArrayList<String>();

        if (snapshotsFile.list() != null) {
            for (String fileName : snapshotsFile.list()) {
                if (fileName.length()==14 && fileName.startsWith("2") && StringUtils.allCharactersAreNumeric(fileName)) {
                    links.add(fileName);
                }
            }
        }

        return links;
    }

    public String getDateFormatForSnapshotId(){
        return SNAPSHOT_ID_PLACEHOLDER;
    }

    private String simpleNameFolder(final Class associatedChannelResourceClazz){
        return associatedChannelResourceClazz.getSimpleName().replace("Resource","");
    }

    private String classBasedFileNameWithExtension(final Class associatedChannelResourceClazz){
        return associatedChannelResourceClazz.getCanonicalName()+getFilenameExtension();
    }

    private String fillOutPath(String templateString, final Class associatedChannelResourceClazz, String snapshotId){
        String result=templateString.replace(CHANNEL_NAME_PLACEHOLDER, simpleNameFolder(associatedChannelResourceClazz));
        if(snapshotId!=null){
            result=result.replace(SNAPSHOT_ID_PLACEHOLDER, snapshotId);
        }
        return result;
    }

    public void takeChannelSnapshot(Class associatedChannelResourceClazz, List<EntryWrapper> channelContents, ChannelState channelState, String snapshotId) {
        takeChannelContentsSnapshot(associatedChannelResourceClazz, channelContents, snapshotId);
        takeChannelStateSnapshot(associatedChannelResourceClazz, channelState, snapshotId);
    }

    public void takeListenerSnapshot(Class associatedChannelResourceClazz, Map<String, MessageListenerAddress> listenerAddresses, Map<String, MessageListenerGroup> listenerRegistration, String snapshotId) {
        takeListenerAddressSnapshot(associatedChannelResourceClazz, listenerAddresses, snapshotId);
        takeListenerRegistrationSnapshot(associatedChannelResourceClazz, listenerRegistration, snapshotId);
    }

    protected void takeListenerRegistrationSnapshot(Class associatedChannelResourceClazz, Map<String, MessageListenerGroup> listenerRegistration, String snapshotId) {
        final String fullFilePath = fillOutPath(LISTENER_REGISTRATION_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        final String fileName = classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(fullFilePath, fileName, new Serializer().toType(listenerRegistration, getFilenameExtensionCode()))){
            log.info("Channel listener registration for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    fullFilePath + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel listener registration have not been saved.");
        }
    }

    protected void takeListenerAddressSnapshot(Class associatedChannelResourceClazz, Map<String, MessageListenerAddress> listenerAddresses, String snapshotId) {
        final String fullFilePath = fillOutPath(LISTENERS_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        final String fileName = classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(fullFilePath, fileName, new Serializer().toType(listenerAddresses, getFilenameExtensionCode()))){
            log.info("Channel listeners for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    fullFilePath + classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel listeners have not been saved.");
        }
    }
}
