package com.restqueue.framework.service.channels;

import com.restqueue.framework.service.persistence.XmlFilePersistence;

import java.util.*;

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
 * <p/>
 * Date: 04/01/2014
 * Time: 02:57
 */
public class ChannelsRegistry {
    private static volatile ChannelsRegistry instance;
    private static final Object LOCK = new Object();
    private Map<Class, ChannelMetadata> channelMetadataMap = new HashMap<Class, ChannelMetadata>();

    public static ChannelsRegistry getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    //load from xml file
                    instance = new XmlFilePersistence().loadChannelsRegistry();
                    if(instance==null){
                        instance = new ChannelsRegistry();
                    }
                }
            }
        }
        return instance;
    }

    private ChannelsRegistry() {
    }

    public void addChannel(Class clazz, ChannelMetadata channelMetadata) {
        if (!this.channelMetadataMap.containsKey(clazz)) {
            this.channelMetadataMap.put(clazz, channelMetadata);
            new XmlFilePersistence().saveChannelsRegistry(this);
        }
    }

    public List<ChannelMetadata> channelsSummary() {
        final List<ChannelMetadata> returnVal = new ArrayList<ChannelMetadata>(this.channelMetadataMap.values());

        Collections.sort(returnVal, new Comparator<ChannelMetadata>() {
            public int compare(ChannelMetadata channelMetadata, ChannelMetadata channelMetadata2) {
                return channelMetadata.getChannelEndPoint().getDescription().compareTo(channelMetadata2.getChannelEndPoint().getDescription());
            }
        });

        return returnVal;
    }
}
