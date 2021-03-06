package com.restqueue.framework.service.backingstoreduplicatesfilters;

import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.exception.ChannelStoreException;
import com.restqueue.framework.service.transport.ServiceRequest;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

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
 * Date: Jan 21, 2011
 * Time: 7:46:14 PM
 */
public class DuplicatesNotAllowedTest {
    @Test(expected = ChannelStoreException.class)
    public void addingDuplicatesShouldNotBeAllowedWithThisFilter(){
        final List<EntryWrapper> listToTryToAddTo = new ArrayList<EntryWrapper>();

        final EntryWrapper entryWrapperOne = new EntryWrapper();
        entryWrapperOne.setContent("hello");
        final EntryWrapper entryWrapperTwo = new EntryWrapper();
        entryWrapperTwo.setContent("hello");

        final DuplicatesNotAllowed duplicatesNotAllowed = new DuplicatesNotAllowed();

        assertEquals(0,listToTryToAddTo.size());
        duplicatesNotAllowed.add(entryWrapperOne, listToTryToAddTo);
        assertEquals(1,listToTryToAddTo.size());
        duplicatesNotAllowed.add(entryWrapperOne, listToTryToAddTo);
        assertEquals(1,listToTryToAddTo.size());
        duplicatesNotAllowed.add(entryWrapperTwo, listToTryToAddTo);
        assertEquals(1,listToTryToAddTo.size());
    }

    @Test(expected = ChannelStoreException.class)
    public void updatingEntryToMakeADuplicateShouldNotBeAllowedWithThisFilter(){
        final List<EntryWrapper> listToTryToAddTo = new ArrayList<EntryWrapper>();

        final EntryWrapper entryWrapperOne = new EntryWrapper();
        entryWrapperOne.setContent("hello");
        entryWrapperOne.setEntryId("1");
        final EntryWrapper entryWrapperTwo = new EntryWrapper();
        entryWrapperTwo.setContent("hello");
        entryWrapperTwo.setEntryId("2");

        final DuplicatesNotAllowed duplicatesNotAllowed = new DuplicatesNotAllowed();

        assertEquals(0,listToTryToAddTo.size());
        duplicatesNotAllowed.add(entryWrapperOne, listToTryToAddTo);
        assertEquals(1,listToTryToAddTo.size());
        duplicatesNotAllowed.add(entryWrapperTwo, listToTryToAddTo);
        assertEquals(2,listToTryToAddTo.size());

        //buildOnly service request here
        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setBody("<string>hello</string>").
                setMediaTypeRequested(MediaType.APPLICATION_XML).addParameter("entryId","2").build();
        try {
            duplicatesNotAllowed.updateFromServiceRequest(entryWrapperTwo, serviceRequest, listToTryToAddTo);
        } catch (ChannelStoreException e) {
            assertEquals(ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED, e.getExceptionType());
            throw e;
        }

    }
}
