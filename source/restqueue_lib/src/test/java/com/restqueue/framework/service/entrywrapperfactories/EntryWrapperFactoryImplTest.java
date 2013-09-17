package com.restqueue.framework.service.entrywrapperfactories;

import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.transport.ServiceHeaders;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

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
 * Time: 7:02:05 PM
 */
public class EntryWrapperFactoryImplTest {
    @Test
    public void factoryCanDealWithEmptyHeadersFromXml(){
        final String stockQueryXml="<com.restqueue.domainentities.StockQuery>\n" +
                "  <stockCode>StockCode1</stockCode>\n" +
                "  <stockAvailable>10</stockAvailable>\n" +
                "  <stockQueryUid>1</stockQueryUid>\n" +
                "</com.restqueue.domainentities.StockQuery>";
        final String linkUri = "linkUri";
        final String id = "12345";
        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().build();
        final EntryWrapper entryWrapper = new EntryWrapperFactoryImpl().newEntryWrapperInstanceFromXml(
                stockQueryXml, id, linkUri, serviceHeaders);
        assertEquals(id, entryWrapper.getEntryId());
        assertEquals(linkUri, entryWrapper.getLinkUri());
        assertNotNull(entryWrapper.getContent());
        assertNull(entryWrapper.getMessageConsumerId());
        assertNull(entryWrapper.getCreator());
        assertNull(entryWrapper.getBatchKey());
        assertNotNull(entryWrapper.getCreated());
        assertNotNull(entryWrapper.getLastUpdated());
        assertNotNull(entryWrapper.getETag());
        assertNotNull(entryWrapper.getReturnAddresses());
        assertEquals(0, entryWrapper.getReturnAddresses().size());
    }

    @Test
    public void factoryCanDealWithEmptyHeadersFromJson(){
        final String stockQueryJson="{\"com.restqueue.domainentities.StockQuery\":{\"stockCode\":\"StockCode1\",\"stockAvailable\":11,\"stockQueryUid\":1}}";
        final String linkUri = "linkUri";
        final String id = "12345";
        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().build();
        final EntryWrapper entryWrapper = new EntryWrapperFactoryImpl().newEntryWrapperInstanceFromJson(
                stockQueryJson, id, linkUri, serviceHeaders);
        assertEquals(id, entryWrapper.getEntryId());
        assertEquals(linkUri, entryWrapper.getLinkUri());
        assertNotNull(entryWrapper.getContent());
        assertNull(entryWrapper.getMessageConsumerId());
        assertNull(entryWrapper.getCreator());
        assertNull(entryWrapper.getBatchKey());
        assertNotNull(entryWrapper.getCreated());
        assertNotNull(entryWrapper.getLastUpdated());
        assertNotNull(entryWrapper.getETag());
        assertNotNull(entryWrapper.getReturnAddresses());
        assertEquals(0, entryWrapper.getReturnAddresses().size());
    }
}
