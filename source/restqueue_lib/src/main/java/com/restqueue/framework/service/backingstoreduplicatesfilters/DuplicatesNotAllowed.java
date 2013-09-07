package com.restqueue.framework.service.backingstoreduplicatesfilters;

import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.exception.ChannelStoreException;
import com.restqueue.framework.service.exception.SerializationException;
import com.restqueue.framework.service.transport.ServiceRequest;
import org.apache.log4j.Logger;

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
 * Time: 5:02:59 PM
 */
public class DuplicatesNotAllowed implements BackingStoreDuplicatesFilter {
    private static final Logger log = Logger.getLogger(DuplicatesNotAllowed.class);

    public EntryWrapper add(EntryWrapper entryToAdd, List<EntryWrapper> listToAddTo) {
        for (EntryWrapper entryWrapper : listToAddTo) {
            if (entryWrapper.getContent() != null && entryWrapper.getContent().equals(entryToAdd.getContent())) {
                final String message = "Messages with duplicate content are not allowed - retaining existing message";
                log.warn(message);
                throw new ChannelStoreException(message,
                        ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED);
            }
        }
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

    public void updateFromServiceRequest(EntryWrapper entryWrapperToUpdate, ServiceRequest serviceRequest, List<EntryWrapper> listToUpdate) {
        Object content;
        if (serviceRequest.getBody() != null && !serviceRequest.getBody().trim().equals("")) {
            try {
                content = new Serializer().fromType(serviceRequest.getBody(), serviceRequest.getMediaTypeRequested());
            } catch (Exception e) {
                throw new SerializationException("Invalid content provided:" + serviceRequest.getBody(), e);
            }

            final BatchKey batchKey = entryWrapperToUpdate.getBatchKey();

            for (EntryWrapper entryWrapper : listToUpdate) {
                if (entryWrapper.getContent() != null && entryWrapper.getContent().equals(content) &&
                        !entryWrapper.getEntryId().equals(entryWrapperToUpdate.getEntryId())) {
                    final String message = "Messages with duplicate content are not allowed - refusing update";
                    log.warn(message);
                    throw new ChannelStoreException(message,ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED);
                }
                if(batchKey!=null && batchKey.equals(entryWrapper.getBatchKey())){
                    final String message = "Messages with duplicate batch keys are not allowed";
                    log.warn(message);
                    throw new ChannelStoreException(message,
                            ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED);
                }
            }
        }

        entryWrapperToUpdate.updateFromServiceRequest(serviceRequest);
    }
}
