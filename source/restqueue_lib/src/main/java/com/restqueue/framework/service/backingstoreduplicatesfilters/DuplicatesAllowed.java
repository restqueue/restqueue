package com.restqueue.framework.service.backingstoreduplicatesfilters;

import com.restqueue.common.utils.StringUtils;
import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ExpiryDate;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.exception.ChannelStoreException;
import com.restqueue.framework.service.exception.SerializationException;
import com.restqueue.framework.service.transport.ServiceRequest;
import org.apache.log4j.Logger;

import java.util.Arrays;
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
 * Date: Dec 29, 2010
 * Time: 5:00:58 PM
 */
public class DuplicatesAllowed extends BackingStoreDuplicatesFilter{
    private static final Logger log = Logger.getLogger(DuplicatesAllowed.class);

    public EntryWrapper add(EntryWrapper entryToAdd, List<EntryWrapper> listToAddTo) {
        if(entryToAdd.getBatchKey()!=null){
            final BatchKey batchKey = entryToAdd.getBatchKey();
            for(EntryWrapper entryWrapper:listToAddTo){
                if(batchKey!=null && batchKey.equals(entryWrapper.getBatchKey())){
                    final String message = "Messages with duplicate batch keys are not allowed";
                    log.warn(message);
                    throw new ChannelStoreException(message,
                            ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED);
                }
            }
        }
        listToAddTo.add(entryToAdd);
        return entryToAdd;
    }

    public void updateFromServiceRequest(EntryWrapper entryToUpdate, ServiceRequest serviceRequest, List<EntryWrapper> listToUpdate) {
        if(entryToUpdate.getBatchKey()!=null){
            final BatchKey batchKey = entryToUpdate.getBatchKey();
            for(EntryWrapper entryWrapper:listToUpdate){
                if(!entryWrapper.getEntryId().equals(entryToUpdate.getEntryId()) && batchKey!=null && batchKey.equals(entryWrapper.getBatchKey())){
                    final String message = "Messages with duplicate batch keys are not allowed";
                    log.warn(message);
                    throw new ChannelStoreException(message,
                            ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED);
                }
            }
        }
        updateEntryWrapper(entryToUpdate, serviceRequest);
    }
}
