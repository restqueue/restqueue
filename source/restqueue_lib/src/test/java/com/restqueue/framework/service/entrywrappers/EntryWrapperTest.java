package com.restqueue.framework.service.entrywrappers;

import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.backingstoreduplicatesfilters.DuplicatesAllowed;
import com.restqueue.framework.service.exception.SerializationException;
import com.restqueue.framework.service.transport.ServiceHeaders;
import com.restqueue.framework.service.transport.ServiceRequest;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
 * Date: Jan 11, 2011
 * Time: 7:28:15 PM
 */
public class EntryWrapperTest {
    @Test
    public void entryWrapperShouldUpdateOnlyUserMutableFieldsFromServiceRequest() {
        final String content = "<com.restqueue.framework.client.common.entryfields.ReturnAddress><type>URL</type><address>address</address></com.restqueue.framework.client.common.entryfields.ReturnAddress>";
        final String creator = "Creator";
        final String emailReturnAddress = "me@there.com";
        final String channelReturnAddress = "http://localhost:9998/channels/1.0/stockQueryResultsQueue";
        final String messageConsumer = "messageConsumer";
        final String rubbish = "rubbish";

        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                addHeader(CustomHeaders.CREATOR, creator).
                addHeader(CustomHeaders.LAST_MODIFIED, rubbish).
                addHeader(CustomHeaders.ETAG, rubbish).
                addHeader(CustomHeaders.LOCATION, rubbish).
                addHeader(CustomHeaders.RETURN_ADDRESSES,new ReturnAddress(ReturnAddressType.EMAIL,emailReturnAddress).format()).
                addHeader(CustomHeaders.RETURN_ADDRESSES,new ReturnAddress(ReturnAddressType.URL,channelReturnAddress).format()).
                addHeader(CustomHeaders.MESSAGE_CONSUMER_ID, messageConsumer).
                build();

        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(MediaType.APPLICATION_XML).
                setBody(content).setServiceHeaders(serviceHeaders).build();

        final ReturnAddress emailReturnAddressObject = new ReturnAddress(ReturnAddressType.EMAIL, emailReturnAddress);
        final ReturnAddress urlReturnAddressObject = new ReturnAddress(ReturnAddressType.URL, channelReturnAddress);

        final EntryWrapper newEntryWrapper = new EntryWrapper();

        new DuplicatesAllowed().updateEntryWrapper(newEntryWrapper, serviceRequest);

        assertEquals(new Serializer().fromType(content,MediaType.APPLICATION_XML), newEntryWrapper.getContent());
        assertEquals(messageConsumer, newEntryWrapper.getMessageConsumerId());
        assertNull(newEntryWrapper.getCreator());//should not set this as it is not a mutable field
        assertTrue(!rubbish.equals(newEntryWrapper.getLastUpdated()));//should not set this as it is not a mutable field
        assertTrue(!rubbish.equals(newEntryWrapper.getCreated()));//should not set this as it is not a mutable field
        assertTrue(!rubbish.equals(newEntryWrapper.getETag()));//should not set this as it is not a mutable field
        assertTrue(!rubbish.equals(newEntryWrapper.getLinkUri()));//should not set this as it is not a mutable field
        assertNotNull(newEntryWrapper.getReturnAddresses());
        assertTrue(newEntryWrapper.getReturnAddresses().size()==2);
        assertEquals(emailReturnAddressObject,newEntryWrapper.getReturnAddresses().get(0));
        assertEquals(urlReturnAddressObject,newEntryWrapper.getReturnAddresses().get(1));
    }

    @Test
    public void entryWrapperShouldOnlyUpdateFieldsFromServiceRequest() {
        final String content = "<com.restqueue.framework.client.common.entryfields.ReturnAddress><type>URL</type><address>address</address></com.restqueue.framework.client.common.entryfields.ReturnAddress>";
        final String creator = "Creator";
        final String emailReturnAddress = "me@there.com";
        final String channelReturnAddress = "http://localhost:9998/channels/1.0/stockQueryResultsQueue";

        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(MediaType.APPLICATION_XML).
                setBody(content).build();

        final ReturnAddress emailReturnAddressObject = new ReturnAddress(ReturnAddressType.EMAIL, emailReturnAddress);
        final ReturnAddress urlReturnAddressObject = new ReturnAddress(ReturnAddressType.URL, channelReturnAddress);

        final EntryWrapper newEntryWrapper = new EntryWrapper();
        newEntryWrapper.setCreator(creator);
        newEntryWrapper.addReturnAddress(emailReturnAddressObject);
        newEntryWrapper.addReturnAddress(urlReturnAddressObject);

        new DuplicatesAllowed().updateEntryWrapper(newEntryWrapper, serviceRequest);

        assertEquals(new Serializer().fromType(content,MediaType.APPLICATION_XML), newEntryWrapper.getContent());
        assertNull(newEntryWrapper.getMessageConsumerId());
        assertNotNull(newEntryWrapper.getReturnAddresses());
        assertTrue(newEntryWrapper.getReturnAddresses().size()==2);
        assertEquals(emailReturnAddressObject,newEntryWrapper.getReturnAddresses().get(0));
        assertEquals(urlReturnAddressObject,newEntryWrapper.getReturnAddresses().get(1));
    }

    @Test
    public void entryWrapperShouldNotUpdateContentIfNull() {
        final String content="content";

        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(MediaType.APPLICATION_XML).
                build();

        final EntryWrapper newEntryWrapper = new EntryWrapper();
        newEntryWrapper.setContent(content);

        new DuplicatesAllowed().updateEntryWrapper(newEntryWrapper, serviceRequest);

        assertEquals(content, newEntryWrapper.getContent());
    }

    @Test
    public void entryWrapperShouldNotUpdateContentIfEmpty() {
        final String content="content";

        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(MediaType.APPLICATION_XML).
                setBody("").build();

        final EntryWrapper newEntryWrapper = new EntryWrapper();
        newEntryWrapper.setContent(content);

        new DuplicatesAllowed().updateEntryWrapper(newEntryWrapper, serviceRequest);

        assertEquals(content, newEntryWrapper.getContent());
    }

    @Test
    public void entryWrapperShouldNotUpdateContentIfBlank() {
        final String content="content";

        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(MediaType.APPLICATION_XML).
                setBody("   ").build();

        final EntryWrapper newEntryWrapper = new EntryWrapper();
        newEntryWrapper.setContent(content);

        new DuplicatesAllowed().updateEntryWrapper(newEntryWrapper, serviceRequest);

        assertEquals(content, newEntryWrapper.getContent());
    }

    @Test(expected= SerializationException.class)
    public void entryWrapperShouldThrowSerializationExceptionIfGivenInvalidContent() {
        final String content="content";

        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(MediaType.APPLICATION_XML).
                setBody(content).build();

        final EntryWrapper newEntryWrapper = new EntryWrapper();
        newEntryWrapper.setContent(content);

        new DuplicatesAllowed().updateEntryWrapper(newEntryWrapper, serviceRequest);

        assertEquals(content, newEntryWrapper.getContent());
    }
}
