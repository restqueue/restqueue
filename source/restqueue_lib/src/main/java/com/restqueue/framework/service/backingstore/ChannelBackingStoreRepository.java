package com.restqueue.framework.service.backingstore;

import com.restqueue.framework.service.backingstoreduplicatesfilters.BackingStoreDuplicatesFilter;
import com.restqueue.framework.service.backingstorefilters.BackingStoreFilter;

import java.util.HashMap;
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
 * Date: Dec 23, 2010
 * Time: 4:23:43 PM
 */
public final class ChannelBackingStoreRepository {
    private static final Object LOCK=new Object();

    private static Map<Class, ChannelBackingStore> channelBackingStoreMap = new HashMap<Class, ChannelBackingStore>();

    public static ChannelBackingStore getOrCreateInstance(Class clazz, BackingStoreFilter backingStoreFilter, BackingStoreDuplicatesFilter backingStoreDuplicatesFilter) {
        if (channelBackingStoreMap.get(clazz) == null) {
            synchronized (LOCK) {
                if (channelBackingStoreMap.get(clazz) == null) {
                    channelBackingStoreMap.put(clazz, ChannelBackingStore.getBoundedInstance(backingStoreFilter, backingStoreDuplicatesFilter, clazz));
                    channelBackingStoreMap.get(clazz).restoreFromPersistedState();
                }
            }
        }
        return channelBackingStoreMap.get(clazz);
    }

    public static ChannelBackingStore getInstance(Class clazz) {
        return channelBackingStoreMap.get(clazz);
    }
}
