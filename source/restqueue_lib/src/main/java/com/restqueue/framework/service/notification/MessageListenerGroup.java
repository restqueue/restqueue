package com.restqueue.framework.service.notification;

import com.restqueue.framework.service.backingstorefilters.BackingStoreFilter;

import java.util.HashSet;
import java.util.Set;

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
 * Date: Apr 30, 2011
 * Time: 7:59:18 PM
 */
public class MessageListenerGroup {
    private BackingStoreFilter backingStoreFilter;
    private Object[] filterArguments;
    private Set<String> listenerIds =new HashSet<String>();

    public void setBackingStoreFilter(BackingStoreFilter backingStoreFilter) {
        this.backingStoreFilter = backingStoreFilter;
    }

    public void setFilterArguments(Object[] filterArguments) {
        this.filterArguments = filterArguments;
    }

    public void addListenerId(final String listenerId){
        this.listenerIds.add(listenerId);
    }

    public void removeListenerId(final String listenerId){
        this.listenerIds.remove(listenerId);
    }

    public BackingStoreFilter getBackingStoreFilter() {
        return backingStoreFilter;
    }

    public Object[] getFilterArguments() {
        return filterArguments;
    }

    public Set<String> getListenerIds() {
        return listenerIds;
    }
}
