package com.restqueue.framework.service.backingstoreduplicatesfilters;

import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.transport.ServiceRequest;

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
 * Date: Dec 29, 2010
 * Time: 4:59:23 PM
 */
public interface BackingStoreDuplicatesFilter {
    public EntryWrapper add(EntryWrapper entryToAdd, List<EntryWrapper> listToAddTo);
    public void updateFromServiceRequest(EntryWrapper entryWrapper, ServiceRequest serviceRequest, List<EntryWrapper> listToUpdate);
}
