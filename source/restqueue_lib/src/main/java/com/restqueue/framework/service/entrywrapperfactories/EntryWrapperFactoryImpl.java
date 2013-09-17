package com.restqueue.framework.service.entrywrapperfactories;

import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.transport.ServiceHeaders;

import javax.ws.rs.core.MediaType;


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
 * Date: Jan 5, 2011
 * Time: 8:07:34 PM
 */
public class EntryWrapperFactoryImpl implements EntryWrapperFactory {
    public EntryWrapper newEntryWrapperInstanceFromXml(String requestBody, String entryId, String linkUri, ServiceHeaders headers) {
        final String type = MediaType.APPLICATION_XML;
        return createEntryWrapper(requestBody, entryId, linkUri, headers, type);
    }

    public EntryWrapper newEntryWrapperInstanceFromJson(String requestBody, String entryId, String linkUri, ServiceHeaders headers) {
        final String type = MediaType.APPLICATION_JSON;
        return createEntryWrapper(requestBody, entryId, linkUri, headers, type);
    }

    private EntryWrapper createEntryWrapper(String requestBody, String entryId, String linkUri, ServiceHeaders headers, String type) {
        final EntryWrapper entryWrapper = new EntryWrapper();
        entryWrapper.setContent(new Serializer().fromType(requestBody, type));
        entryWrapper.setCreator(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.CREATOR));
        for (ReturnAddress returnAddress : ReturnAddress.parse(headers.getHeaderValueList(CustomHeaders.RETURN_ADDRESSES))) {
            entryWrapper.addReturnAddress(returnAddress);
        }
        entryWrapper.setSequence(headers.getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_SEQUENCE));
        entryWrapper.setDelayUntil(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY_UNTIL));
        entryWrapper.setDelay(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY));
        entryWrapper.setPriority(headers.getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_PRIORITY));
        entryWrapper.setBatchKey(BatchKey.parse(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_BATCH_KEY)));
        entryWrapper.setEntryId(entryId);
        entryWrapper.setLinkUri(linkUri);
        return entryWrapper;
    }
}
