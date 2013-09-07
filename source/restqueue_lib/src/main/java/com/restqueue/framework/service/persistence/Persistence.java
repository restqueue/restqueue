package com.restqueue.framework.service.persistence;

import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.notification.MessageListenerAddress;
import com.restqueue.framework.service.notification.MessageListenerGroup;

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
 * Time: 6:54:58 PM
 */
public interface Persistence {
    public static final String MESSAGE_LISTENERS_KEY = "messagelisteners";
    public static final String MESSAGE_LISTENER_REGISTRATION_KEY = "messagelistenerregistration";
    public static final String CHANNEL_STATE_KEY = "channelstate";
    public static final String CHANNEL_CONTENTS_KEY = "channelcontents";
    public static final String FRAMEWORK_NAME = "restqueue";

    public void saveUpdated(final Class associatedChannelResourceClazz, Map<String, Object> changedState);

    public Map<String, MessageListenerAddress> loadMessageListeners(final Class associatedChannelResourceClazz);

    public Map<String, MessageListenerGroup> loadMessageListenerRegistration(final Class associatedChannelResourceClazz);

    public ChannelState loadChannelState(final Class associatedChannelResourceClazz);

    public void takeChannelSnapshot(final Class associatedChannelResourceClazz, final List<EntryWrapper> channelContents,
                                    final ChannelState channelState, final String snapshotId);

    public void takeListenerSnapshot(final Class associatedChannelResourceClazz, final Map<String, MessageListenerAddress> listenerAddresses,
                                     Map<String, MessageListenerGroup> listenerRegistration, final String snapshotId);

    public List<EntryWrapper> loadChannelContents(final Class associatedChannelResourceClazz);

    public List<EntryWrapper> loadChannelContentsSnapshot(final Class associatedChannelResourceClazz, final String snapshotId);

    public void overwriteCurrentDataWithSnapshot(final Class associatedChannelResourceClazz, final String snapshotId);

    public void overwriteCurrentListenerDataWithSnapshot(final Class associatedChannelResourceClazz, final String snapshotId);

    public List<String> getSnapshotList(final Class associatedChannelResourceClazz);

    public String getDateFormatForSnapshotId();
}
