package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

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
 * Date: 20/08/2013
 * Time: 20:58
 */
public class ExpiredFilterTest {
    @Test
    public void filterShouldProvideExpiredMessagesOnly() throws InterruptedException {
        final List<EntryWrapper> listOfEntryWrappersBefore = new ArrayList<EntryWrapper>();

        final EntryWrapper entryWrapper0 = new EntryWrapper();
        entryWrapper0.setDelay("10");
        final EntryWrapper entryWrapper4 = new EntryWrapper();
        entryWrapper4.setDelay("20");

        listOfEntryWrappersBefore.add(entryWrapper0);
        listOfEntryWrappersBefore.add(entryWrapper4);

        List<EntryWrapper> listOfEntryWrappersAfter = new ExpiredFilter(new ArrivalOrderFilter()).
                        filter(listOfEntryWrappersBefore, null, new Object[0]);
        assertEquals(0,listOfEntryWrappersAfter.size());

        Thread.sleep(11000);

        listOfEntryWrappersAfter = new ExpiredFilter(new ArrivalOrderFilter()).
                filter(listOfEntryWrappersBefore, null, new Object[0]);

        assertEquals(1, listOfEntryWrappersAfter.size());

        //wait another 10 seconds
        Thread.sleep(10000);

        listOfEntryWrappersAfter = new ExpiredFilter(new ArrivalOrderFilter()).
                filter(listOfEntryWrappersBefore, null, new Object[0]);

        assertEquals(2, listOfEntryWrappersAfter.size());

    }
}
