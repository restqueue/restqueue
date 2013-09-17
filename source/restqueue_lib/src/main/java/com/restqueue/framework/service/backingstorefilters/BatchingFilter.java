package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.framework.service.channelstate.BatchStrategy;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;

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
 * Date: Feb 10, 2011
 * Time: 9:22:04 AM
 */
public class BatchingFilter implements BackingStoreFilter {
    private static Map<BatchStrategy, BackingStoreFilter> batchingStrategyFilters=new HashMap<BatchStrategy, BackingStoreFilter>();

    static{
        batchingStrategyFilters.put(BatchStrategy.ARRIVAL, new ArrivalBatchFilter());
        batchingStrategyFilters.put(BatchStrategy.COMPLETE, new CompleteBatchFilter());
    }

    @SuppressWarnings("unchecked")
    public List<EntryWrapper> filter(List<EntryWrapper> listOfEntries, ChannelState channelState, Object[] arguments) {

        return batchingStrategyFilters.get(BatchStrategy.valueOf(
                String.valueOf(channelState.getFieldValue(ChannelState.BATCH_STRATEGY)))).filter(listOfEntries, channelState, arguments);
    }
}

