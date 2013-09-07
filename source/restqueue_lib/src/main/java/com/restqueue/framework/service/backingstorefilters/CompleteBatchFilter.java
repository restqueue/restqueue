package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;

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
 * Date: Feb 11, 2011
 * Time: 7:04:38 PM
 */
public class CompleteBatchFilter implements BackingStoreFilter{

    @SuppressWarnings("unchecked")
    public List<EntryWrapper> filter(List<EntryWrapper> listOfEntries, ChannelState channelState, Object[] arguments) {
        int batchSize=0;

        final List<EntryWrapper> listToReturn = new ArrayList<EntryWrapper>();

        final String batchId=(String)arguments[0];

        for(EntryWrapper entryWrapper:listOfEntries){
            if(entryWrapper.getBatchKey()!=null && batchId.equals(entryWrapper.getBatchKey().getBatchId())){
                listToReturn.add(entryWrapper);
                if(entryWrapper.getBatchKey().getBatchSize()>batchSize){
                    batchSize=entryWrapper.getBatchKey().getBatchSize();
                }
            }
        }

        if(listToReturn.size()>=batchSize){
            return listToReturn;
        }
        else{
            return new ArrayList<EntryWrapper>();
        }
    }
}
