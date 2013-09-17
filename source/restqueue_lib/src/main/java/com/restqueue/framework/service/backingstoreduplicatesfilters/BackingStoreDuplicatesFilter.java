package com.restqueue.framework.service.backingstoreduplicatesfilters;

import com.restqueue.common.utils.StringUtils;
import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ExpiryDate;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.exception.SerializationException;
import com.restqueue.framework.service.transport.ServiceRequest;

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
 * Time: 4:59:23 PM
 */
public abstract class BackingStoreDuplicatesFilter {
    public abstract EntryWrapper add(EntryWrapper entryToAdd, List<EntryWrapper> listToAddTo);
    public abstract void updateFromServiceRequest(EntryWrapper entryWrapper, ServiceRequest serviceRequest, List<EntryWrapper> listToUpdate);
    public void updateEntryWrapper(EntryWrapper entryWrapper, ServiceRequest serviceRequest) {
    if(serviceRequest.getBody()!=null && !serviceRequest.getBody().trim().equals("")){
        try {
            entryWrapper.setContent(new Serializer().fromType(serviceRequest.getBody(), serviceRequest.getMediaTypeRequested()));
        } catch (Exception e) {
            throw new SerializationException("Invalid content provided:"+serviceRequest.getBody(),e);
        }
    }

    if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_CONSUMER_ID)){
        entryWrapper.setMessageConsumerId(serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_CONSUMER_ID));
    }

    if (serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.RETURN_ADDRESSES)) {
        entryWrapper.getReturnAddresses().clear();
        for (ReturnAddress returnAddress : Arrays.asList(ReturnAddress.parse(serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.RETURN_ADDRESSES)))) {
            entryWrapper.addReturnAddress(returnAddress);
        }
    }
    if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_DELAY_UNTIL)){
        entryWrapper.setDelayUntil(serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY_UNTIL));
    }
    if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_DELAY)){
        entryWrapper.setDelay(serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY));
    }
    if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_SEQUENCE)){
        entryWrapper.setSequence(serviceRequest.getServiceHeaders().getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_SEQUENCE));
    }
    if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_PRIORITY)){
        entryWrapper.setPriority(serviceRequest.getServiceHeaders().getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_PRIORITY));
    }
    if (serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_BATCH_KEY)) {
        entryWrapper.setBatchKey(BatchKey.parse(serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_BATCH_KEY)));
    }

    //if the delay until has been set, use this, otherwise set the delay until from the delay header and created date
    if (StringUtils.isNullOrEmpty(entryWrapper.getDelayUntil())) {
        if (StringUtils.isNotNullAndNotEmpty(entryWrapper.getDelay())) {
            entryWrapper.setDelayUntil(ExpiryDate.fromDelayHeader(entryWrapper.getDelay()).toExpiryDateHeader(entryWrapper.getCreated()));
        }
    }

    entryWrapper.setLastUpdated(System.currentTimeMillis());
}}
