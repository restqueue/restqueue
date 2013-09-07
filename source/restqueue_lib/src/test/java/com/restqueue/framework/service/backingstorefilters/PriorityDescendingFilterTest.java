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
 * Date: Feb 5, 2011
 * Time: 6:35:37 PM
 */
public class PriorityDescendingFilterTest {
    @Test
    public void filterShouldOrderListByDescendingPriority(){
        final List<EntryWrapper> listOfEntryWrappersBefore = new ArrayList<EntryWrapper>();

        final EntryWrapper entryWrapper10 = new EntryWrapper.EntryWrapperBuilder().setPriority(10).buildNow();
        final EntryWrapper entryWrapper60 = new EntryWrapper.EntryWrapperBuilder().setPriority(60).buildNow();
        final EntryWrapper entryWrapper30 = new EntryWrapper.EntryWrapperBuilder().setPriority(30).buildNow();

        listOfEntryWrappersBefore.add(entryWrapper30);
        listOfEntryWrappersBefore.add(entryWrapper60);
        listOfEntryWrappersBefore.add(entryWrapper10);

        final List<EntryWrapper> listOfEntryWrappersAfter = new PriorityDescendingFilter(new ArrivalOrderFilter()).
                filter(listOfEntryWrappersBefore, null, new Object[0]);

        assertEquals(listOfEntryWrappersAfter.get(0),entryWrapper60);
        assertEquals(listOfEntryWrappersAfter.get(1),entryWrapper30);
        assertEquals(listOfEntryWrappersAfter.get(2),entryWrapper10);

    }
}
