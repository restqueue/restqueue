package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.common.utils.PriorityUtils;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;

import java.util.ArrayList;
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
 * Date: Feb 5, 2011
 * Time: 7:08:45 PM
 */
public class SpecificPriorityFilter implements BackingStoreFilter{
    @SuppressWarnings("unchecked")
    public List<EntryWrapper> filter(List<EntryWrapper> listOfEntries, ChannelState channelState, Object[] arguments) {
        if("all".equals(arguments[0])){
            return new ArrayList<EntryWrapper>(listOfEntries);
        }

        final List<EntryWrapper> listToReturn = new ArrayList<EntryWrapper>();

        final String priority=(String)arguments[0];
        final Map<String, Integer> mapOfPriorities =
                PriorityUtils.sortByValue((Map<String, Integer>) channelState.getFieldValue(ChannelState.PRIORITY_MAP));

        for(EntryWrapper entryWrapper:listOfEntries){
            if(PriorityUtils.getPriorityGroupFromPriorityValueAndMap(entryWrapper.getPriority(), mapOfPriorities).
                    equals(priority)){
                listToReturn.add(entryWrapper);
            }
        }

        return listToReturn;
    }
}
