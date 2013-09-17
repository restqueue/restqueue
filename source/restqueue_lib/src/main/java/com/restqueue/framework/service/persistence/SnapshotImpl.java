package com.restqueue.framework.service.persistence;

import com.restqueue.common.utils.FileUtils;
import com.restqueue.common.utils.StringUtils;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.exception.ChannelStoreException;
import com.restqueue.framework.service.notification.MessageListenerAddress;
import com.restqueue.framework.service.notification.MessageListenerGroup;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2010-2013 Nik Tomkinson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Date: 16/09/2013
 * Time: 18:36
 */
public class SnapshotImpl implements Snapshot{
    private static final String XML_FILENAME_EXTENSION = ".xml";
    private static final Logger log = Logger.getLogger(SnapshotImpl.class);
    private PersistenceHelper persistenceHelper=new PersistenceHelper(XML_FILENAME_EXTENSION,APPLICATION_XML);

    private static final String APPLICATION_XML = MediaType.APPLICATION_XML;

    protected void takeChannelStateSnapshot(final Class associatedChannelResourceClazz, final ChannelState channelState, final String snapshotId){
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.STATE_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        persistenceHelper.saveChannelStateToPath(associatedChannelResourceClazz, channelState, fullFilePath);
    }

    protected void takeChannelContentsSnapshot(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents, final String snapshotId) {
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.CONTENTS_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        persistenceHelper.saveChannelContentsToPath(associatedChannelResourceClazz, channelContents, fullFilePath);
    }

    public List<EntryWrapper> loadChannelContentsSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.CONTENTS_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        return persistenceHelper.loadChannelContentsFromPath(associatedChannelResourceClazz, fullFilePath);
    }

    public void overwriteCurrentDataWithSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){
        //get specified files
        final String snapshotContentsFileName = persistenceHelper.fillOutPath(PersistenceHelper.CONTENTS_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz);
        final String snapshotStateFileName = persistenceHelper.fillOutPath(PersistenceHelper.STATE_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz);

        //copy specified file into working directory
        try {
            FileUtils.copyFile(snapshotContentsFileName, persistenceHelper.fillOutPath(PersistenceHelper.CONTENTS_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
            FileUtils.copyFile(snapshotStateFileName, persistenceHelper.fillOutPath(PersistenceHelper.STATE_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));

        } catch (IOException e) {
            log.error("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage());
            throw new ChannelStoreException("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage(),
                    ChannelStoreException.ExceptionType.FILE_SYSTEM);
        }
    }

    public void overwriteCurrentListenerDataWithSnapshot(final Class associatedChannelResourceClazz, final String snapshotId){
        //get specified files
        final String snapshotListenersFileName = persistenceHelper.fillOutPath(PersistenceHelper.LISTENERS_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz);
        final String snapshotListenerRegistrationFileName = persistenceHelper.fillOutPath(PersistenceHelper.LISTENER_REGISTRATION_SNAPSHOT_FOLDER,associatedChannelResourceClazz,snapshotId)+
                persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz);

        //copy specified file into working directory
        try {
            FileUtils.copyFile(snapshotListenersFileName, persistenceHelper.fillOutPath(PersistenceHelper.MESSAGE_LISTENERS_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
            FileUtils.copyFile(snapshotListenerRegistrationFileName, persistenceHelper.fillOutPath(PersistenceHelper.MESSAGE_LISTENER_REGISTRATION_BASE_FOLDER,associatedChannelResourceClazz,null)+
                    persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));

        } catch (IOException e) {
            log.error("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage());
            throw new ChannelStoreException("Could not restore from snapshot:" + snapshotId + " - " + e.getMessage(),
                    ChannelStoreException.ExceptionType.FILE_SYSTEM);
        }
    }

    public List<String> getSnapshotList(final Class associatedChannelResourceClazz) {
        final File snapshotsFile = new File(persistenceHelper.fillOutPath(PersistenceHelper.SNAPSHOT_BASE_FOLDER, associatedChannelResourceClazz, null));

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
        return PersistenceHelper.SNAPSHOT_ID_PLACEHOLDER;
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
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.LISTENER_REGISTRATION_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        final String fileName = persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(fullFilePath, fileName, new Serializer().toType(listenerRegistration, persistenceHelper.getFilenameExtensionCode()))){
            log.info("Channel listener registration for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    fullFilePath + persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel listener registration have not been saved.");
        }
    }

    protected void takeListenerAddressSnapshot(Class associatedChannelResourceClazz, Map<String, MessageListenerAddress> listenerAddresses, String snapshotId) {
        final String fullFilePath = persistenceHelper.fillOutPath(PersistenceHelper.LISTENERS_SNAPSHOT_FOLDER, associatedChannelResourceClazz, snapshotId);
        final String fileName = persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz);
        if(FileUtils.saveToDisk(fullFilePath, fileName, new Serializer().toType(listenerAddresses, persistenceHelper.getFilenameExtensionCode()))){
            log.info("Channel listeners for "+associatedChannelResourceClazz.getCanonicalName()+" saved to disk at:"+
                    fullFilePath + persistenceHelper.classBasedFileNameWithExtension(associatedChannelResourceClazz));
        }
        else{
            log.info("There was an error saving to disk - the channel listeners have not been saved.");
        }
    }
}
