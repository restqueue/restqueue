package com.restqueue.framework.service.backingstorefilters;

import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
 * Time: 7:10:22 PM
 */
public class GroupedSequencingFilter implements BackingStoreFilter{

    public List<EntryWrapper> filter(List<EntryWrapper> listOfEntries, ChannelState channelState, Object[] arguments) {
        final List<EntryWrapper> listToParse = new ArrayList<EntryWrapper>();
        for (EntryWrapper entryWrapper : listOfEntries) {
            if (entryWrapper.getSequence() >= (Long)arguments[0]) {
                listToParse.add(entryWrapper);
            }
        }

        Collections.sort(listToParse, new SequenceOrderComparator());

        final List<EntryWrapper> listToReturn = new ArrayList<EntryWrapper>();

        long currentSequenceIndex= (Long)arguments[0];
        for (EntryWrapper entryWrapper : listToParse) {
            if(entryWrapper.getSequence()==currentSequenceIndex){
                listToReturn.add(entryWrapper);
                currentSequenceIndex++;
            }
            else{
                break;
            }
        }

        listToParse.clear();

        return listToReturn;
    }

    private class SequenceOrderComparator implements Comparator<EntryWrapper> {

        public int compare(EntryWrapper firstEntryWrapper, EntryWrapper secondEntryWrapper) {
            return firstEntryWrapper.getSequence()>secondEntryWrapper.getSequence()?1:-1;
        }
    }
}

