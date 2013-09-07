package com.restqueue.framework.client.channelmanagement;

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
 * Date: Jan 23, 2011
 * Time: 9:04:35 PM
 */
public class ChannelState {
    private Map<String, String> map = new HashMap<String, String>();

    public static final String SIZE_KEY = "size";
    public static final String MAX_SIZE_KEY = "maxSize";
    public static final String NEXT_MESSAGE_SEQUENCE_KEY = "nextMessageSequence";

    private ChannelState() {
    }

    public long getNextMessageSequence(){
        return Long.parseLong(map.get(NEXT_MESSAGE_SEQUENCE_KEY));
    }

    public long getCurrentChannelSize(){
        return Long.parseLong(map.get(SIZE_KEY));
    }

    public long getChannelMaxSize(){
        return Long.parseLong(map.get(MAX_SIZE_KEY));
    }

    public static ChannelState fromMap(Map<String, String> map){
        final ChannelState channelState = new ChannelState();
        channelState.map=map;
        return channelState;
    }

    @Override
    public String toString() {
        return "ChannelState{" +
                "map=" + map +
                '}';
    }
}
