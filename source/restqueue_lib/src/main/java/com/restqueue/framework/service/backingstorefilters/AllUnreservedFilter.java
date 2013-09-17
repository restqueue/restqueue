package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;

import java.util.ArrayList;
import java.util.List;

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
 * Date: Jan 28, 2011
 * Time: 8:43:26 PM
 */
public class AllUnreservedFilter implements BackingStoreFilter{
    private BackingStoreFilter nextFilter;

    public AllUnreservedFilter(BackingStoreFilter nextFilter) {
        this.nextFilter = nextFilter;
    }
    public List<EntryWrapper> filter(List<EntryWrapper> listOfEntries, ChannelState channelState, Object[] arguments) {
        final List<EntryWrapper> listToReturn=new ArrayList<EntryWrapper>();
        for(EntryWrapper entry:nextFilter.filter(listOfEntries, channelState, arguments)){
            if(entry.getMessageConsumerId()==null || "".equals(entry.getMessageConsumerId())){
                listToReturn.add(entry);
            }
        }
        return listToReturn;
    }
}
