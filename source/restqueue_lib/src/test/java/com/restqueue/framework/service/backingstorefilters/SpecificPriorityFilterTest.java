package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.framework.service.channelstate.ChannelState;
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
 * Time: 7:45:04 PM
 */
public class SpecificPriorityFilterTest {

    @Test
    public void filterShouldFilterByPriority() {
        final List<EntryWrapper> listOfEntryWrappersBefore = new ArrayList<EntryWrapper>();

        final EntryWrapper entryWrapper10 = new EntryWrapper.EntryWrapperBuilder().setPriority(10).buildNow();
        final EntryWrapper entryWrapper20 = new EntryWrapper.EntryWrapperBuilder().setPriority(20).buildNow();
        final EntryWrapper entryWrapper30 = new EntryWrapper.EntryWrapperBuilder().setPriority(30).buildNow();
        final EntryWrapper entryWrapper40 = new EntryWrapper.EntryWrapperBuilder().setPriority(40).buildNow();
        final EntryWrapper entryWrapper60 = new EntryWrapper.EntryWrapperBuilder().setPriority(60).buildNow();
        final EntryWrapper entryWrapper70 = new EntryWrapper.EntryWrapperBuilder().setPriority(70).buildNow();

        listOfEntryWrappersBefore.add(entryWrapper10);
        listOfEntryWrappersBefore.add(entryWrapper20);
        listOfEntryWrappersBefore.add(entryWrapper30);
        listOfEntryWrappersBefore.add(entryWrapper40);
        listOfEntryWrappersBefore.add(entryWrapper60);
        listOfEntryWrappersBefore.add(entryWrapper70);

        final ChannelState channelState = new ChannelState();

        final SpecificPriorityFilter specificPriorityFilter = new SpecificPriorityFilter();

        List<EntryWrapper> listOfEntryWrappersAfter = specificPriorityFilter.filter(listOfEntryWrappersBefore, channelState, new String[]{"high"});
        assertEquals(1, listOfEntryWrappersAfter.size());
        assertEquals(entryWrapper70, listOfEntryWrappersAfter.get(0));


        listOfEntryWrappersAfter = specificPriorityFilter.filter(listOfEntryWrappersBefore, channelState, new String[]{"medium"});
        assertEquals(2, listOfEntryWrappersAfter.size());
        assertEquals(entryWrapper40, listOfEntryWrappersAfter.get(0));
        assertEquals(entryWrapper60, listOfEntryWrappersAfter.get(1));

        listOfEntryWrappersAfter = specificPriorityFilter.filter(listOfEntryWrappersBefore, channelState, new String[]{"low"});
        assertEquals(3, listOfEntryWrappersAfter.size());
        assertEquals(entryWrapper10, listOfEntryWrappersAfter.get(0));
        assertEquals(entryWrapper20, listOfEntryWrappersAfter.get(1));
        assertEquals(entryWrapper30, listOfEntryWrappersAfter.get(2));


        listOfEntryWrappersAfter = specificPriorityFilter.filter(listOfEntryWrappersBefore, channelState, new String[]{"none"});
        assertEquals(0, listOfEntryWrappersAfter.size());


        listOfEntryWrappersAfter = specificPriorityFilter.filter(listOfEntryWrappersBefore, channelState, new String[]{"all"});
        assertEquals(6, listOfEntryWrappersAfter.size());
        assertEquals(entryWrapper10, listOfEntryWrappersAfter.get(0));
        assertEquals(entryWrapper20, listOfEntryWrappersAfter.get(1));
        assertEquals(entryWrapper30, listOfEntryWrappersAfter.get(2));
        assertEquals(entryWrapper40, listOfEntryWrappersAfter.get(3));
        assertEquals(entryWrapper60, listOfEntryWrappersAfter.get(4));
        assertEquals(entryWrapper70, listOfEntryWrappersAfter.get(5));
    }
}
