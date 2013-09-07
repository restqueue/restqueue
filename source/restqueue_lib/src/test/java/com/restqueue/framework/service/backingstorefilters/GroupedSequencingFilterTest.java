package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.framework.service.entrywrappers.EntryWrapper;
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
 * Date: Mar 4, 2011
 * Time: 7:31:13 PM
 */
public class GroupedSequencingFilterTest {
    @Test
    public void filterShouldReturnCorrectGroupGivenAllAvailable(){
        List<EntryWrapper> entryWrappers = new ArrayList<EntryWrapper>();
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(2).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(1).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(3).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(0).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(4).build());
        final List<EntryWrapper> wrappersReturned = new GroupedSequencingFilter().filter(entryWrappers, null, new Object[]{2L});

        assertEquals(3, wrappersReturned.size());
        assertEquals(2,wrappersReturned.get(0).getSequence());
        assertEquals(3,wrappersReturned.get(1).getSequence());
        assertEquals(4,wrappersReturned.get(2).getSequence());
    }

    @Test
    public void filterShouldReturnCorrectGroupGivenSomeAvailable(){
        List<EntryWrapper> entryWrappers = new ArrayList<EntryWrapper>();
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(2).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(1).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(3).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(0).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(5).build());
        final List<EntryWrapper> wrappersReturned = new GroupedSequencingFilter().filter(entryWrappers, null, new Object[]{2L});

        assertEquals(2, wrappersReturned.size());
        assertEquals(2,wrappersReturned.get(0).getSequence());
        assertEquals(3,wrappersReturned.get(1).getSequence());
    }

    @Test
    public void filterShouldReturnEmptyGroupGivenEmptyList(){
        List<EntryWrapper> entryWrappers = new ArrayList<EntryWrapper>();
        final List<EntryWrapper> wrappersReturned = new GroupedSequencingFilter().filter(entryWrappers, null, new Object[]{2L});

        assertEquals(0, wrappersReturned.size());
    }

    @Test
    public void filterShouldReturnEmptyGroupGivenHigherNextSequenceNumber(){
        List<EntryWrapper> entryWrappers = new ArrayList<EntryWrapper>();
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(2).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(1).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(3).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(0).build());
        entryWrappers.add(new EntryWrapper.EntryWrapperBuilder().setSequence(4).build());
        final List<EntryWrapper> wrappersReturned = new GroupedSequencingFilter().filter(entryWrappers, null, new Object[]{8L});

        assertEquals(0, wrappersReturned.size());
    }
}
