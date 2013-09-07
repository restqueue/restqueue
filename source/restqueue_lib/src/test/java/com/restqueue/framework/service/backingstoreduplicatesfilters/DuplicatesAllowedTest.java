package com.restqueue.framework.service.backingstoreduplicatesfilters;

import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.transport.ServiceRequest;
import org.junit.Test;

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
 * Time: 11:57:46 PM
 */
public class DuplicatesAllowedTest {
    @Test
    public void addingDuplicatesShouldBeAllowedWithThisFilter(){
        final List<EntryWrapper> listToTryToAddTo = new ArrayList<EntryWrapper>();
        final EntryWrapper entryWrapperOne = new EntryWrapper.EntryWrapperBuilder().setContent("hello").buildNow();
        final EntryWrapper entryWrapperTwo = new EntryWrapper.EntryWrapperBuilder().setContent("hello").buildNow();

        final DuplicatesAllowed duplicatesAllowed = new DuplicatesAllowed();

        assertEquals(0,listToTryToAddTo.size());
        duplicatesAllowed.add(entryWrapperOne, listToTryToAddTo);
        assertEquals(1,listToTryToAddTo.size());
        duplicatesAllowed.add(entryWrapperOne, listToTryToAddTo);
        assertEquals(2,listToTryToAddTo.size());
        duplicatesAllowed.add(entryWrapperTwo, listToTryToAddTo);
        assertEquals(3,listToTryToAddTo.size());
    }

    @Test
    public void updatingEntryToMakeADuplicateShouldBeAllowedWithThisFilter(){
        final List<EntryWrapper> listToTryToAddTo = new ArrayList<EntryWrapper>();
        final EntryWrapper entryWrapperOne = new EntryWrapper.EntryWrapperBuilder().setContent("hello").buildNow();
        final EntryWrapper entryWrapperTwo = new EntryWrapper.EntryWrapperBuilder().setContent("goodbye").buildNow();

        final DuplicatesAllowed duplicatesAllowed = new DuplicatesAllowed();

        assertEquals(0,listToTryToAddTo.size());
        duplicatesAllowed.add(entryWrapperOne, listToTryToAddTo);
        assertEquals(1,listToTryToAddTo.size());
        duplicatesAllowed.add(entryWrapperTwo, listToTryToAddTo);
        assertEquals(2,listToTryToAddTo.size());

        //build service request here
        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setBody("<string>hello</string>").
                setMediaTypeRequested("application/xml").build();
        duplicatesAllowed.updateFromServiceRequest(entryWrapperTwo, serviceRequest, listToTryToAddTo);

        assertEquals("hello",entryWrapperTwo.getContent());
    }
}
