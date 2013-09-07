package com.restqueue.framework.service.persistence;

import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.notification.MessageListenerAddress;
import com.restqueue.framework.service.notification.MessageListenerGroup;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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
 * Date: Jan 29, 2012
 * Time: 12:50:41 AM
 */
public class DoNothingPersistence implements Persistence{
    private static final Logger log = Logger.getLogger(DoNothingPersistence.class);
    private static final String SNAPSHOT_ID_PLACEHOLDER = "yyyyMMddHHmmss";

    public void saveUpdated(Class associatedChannelResourceClazz, Map<String, Object> changedState) {
        log.info("Currently using DoNothingPersistence, so no changes will be saved!");
    }

    public Map<String, MessageListenerAddress> loadMessageListeners(final Class associatedChannelResourceClazz) {
        log.info("Currently using DoNothingPersistence, so MessageListeners will not be loaded!");
        return new HashMap<String, MessageListenerAddress>();
    }

    public Map<String, MessageListenerGroup> loadMessageListenerRegistration(final Class associatedChannelResourceClazz) {
        log.info("Currently using DoNothingPersistence, so MessageListener registration data will not be loaded!");
        return new HashMap<String, MessageListenerGroup>();
    }

    public ChannelState loadChannelState(final Class associatedChannelResourceClazz) {
        log.info("Currently using DoNothingPersistence, so channel state will not be loaded!");
        return new ChannelState();
    }

    public void takeChannelSnapshot(Class associatedChannelResourceClazz, List<EntryWrapper> channelContents, ChannelState channelState, String snapshotId) {
        takeChannelContentsSnapshot(associatedChannelResourceClazz, channelContents, snapshotId);
        takeChannelStateSnapshot(associatedChannelResourceClazz, channelState, snapshotId);
    }

    public void takeListenerSnapshot(Class associatedChannelResourceClazz, Map<String, MessageListenerAddress> listenerAddresses, Map<String, MessageListenerGroup> listenerRegistration, String snapshotId) {
        log.info("Currently using DoNothingPersistence, so listener or registration snapshot will not be taken!");
    }

    public void takeChannelStateSnapshot(final Class associatedChannelResourceClazz, final ChannelState channelState, final String snapshotId) {
        log.info("Currently using DoNothingPersistence, so channel state snapshot will not be taken!");
    }

    public List<EntryWrapper> loadChannelContents(final Class associatedChannelResourceClazz) {
        log.info("Currently using DoNothingPersistence, so channel contents will not be loaded!");
        return new ArrayList<EntryWrapper>();
    }

    public void takeChannelContentsSnapshot(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents, final String snapshotId) {
        log.info("Currently using DoNothingPersistence, so channel contents snapshot will not be taken!");
    }

    public List<EntryWrapper> loadChannelContentsSnapshot(final Class associatedChannelResourceClazz, final String snapshotId) {
        log.info("Currently using DoNothingPersistence, so channel contents snapshot will not be loaded!");
        return new ArrayList<EntryWrapper>();
    }

    public void overwriteCurrentDataWithSnapshot(final Class associatedChannelResourceClazz, final String snapshotId) {
        log.info("Currently using DoNothingPersistence, so will not restore contents from snapshot!");
    }

    public void overwriteCurrentListenerDataWithSnapshot(Class associatedChannelResourceClazz, String snapshotId) {
        log.info("Currently using DoNothingPersistence, so will not restore listener data from snapshot!");
    }

    public List<String> getSnapshotList(final Class associatedChannelResourceClazz) {
        log.info("Currently using DoNothingPersistence, so cannot get snapshot list!");
        return new ArrayList<String>();
    }

    public String getDateFormatForSnapshotId() {
        return SNAPSHOT_ID_PLACEHOLDER;
    }
}
