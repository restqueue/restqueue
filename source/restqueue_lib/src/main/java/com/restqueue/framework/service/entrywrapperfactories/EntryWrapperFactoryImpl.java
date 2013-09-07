package com.restqueue.framework.service.entrywrapperfactories;

import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.transport.ServiceHeaders;


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
    public EntryWrapper newEntryWrapperInstance(Object messageBody, String messageProducer) {
        return new EntryWrapper.EntryWrapperBuilder().
                setContent(messageBody).setCreator(messageProducer).buildNow();
    }

    public EntryWrapper newEntryWrapperInstanceFromXml(String requestBody, String entryId, String linkUri, ServiceHeaders headers) {
        return new EntryWrapper.EntryWrapperFromXmlBuilder(requestBody).
                setCreator(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.CREATOR)).
                addReturnAddress(ReturnAddress.parse(headers.getHeaderValueList(CustomHeaders.RETURN_ADDRESSES))).
                setSequence(headers.getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_SEQUENCE)).
                setDelayUntil(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY_UNTIL)).
                setDelay(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY)).
                setPriority(headers.getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_PRIORITY)).
                setBatchKey(BatchKey.parse(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_BATCH_KEY))).
                setEntryId(entryId).setLinkUri(linkUri).build();
    }

    public EntryWrapper newEntryWrapperInstanceFromJson(String requestBody, String entryId, String linkUri, ServiceHeaders headers) {
        return new EntryWrapper.EntryWrapperFromJsonBuilder(requestBody).
                setCreator(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.CREATOR)).
                addReturnAddress(ReturnAddress.parse(headers.getHeaderValueList(CustomHeaders.RETURN_ADDRESSES))).
                setSequence(headers.getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_SEQUENCE)).
                setDelayUntil(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY_UNTIL)).
                setDelay(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY)).
                setPriority(headers.getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_PRIORITY)).
                setBatchKey(BatchKey.parse(headers.getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_BATCH_KEY))).
                setEntryId(entryId).setLinkUri(linkUri).build();
    }
}
