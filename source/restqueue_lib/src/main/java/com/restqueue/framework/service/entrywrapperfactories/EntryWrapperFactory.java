package com.restqueue.framework.service.entrywrapperfactories;

import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.transport.ServiceHeaders;

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
 * Date: Jan 5, 2011
 * Time: 7:41:28 PM
 */
public interface EntryWrapperFactory {
    public EntryWrapper newEntryWrapperInstanceFromXml(String requestBody, String entryId, String linkUri, ServiceHeaders headers);

    public EntryWrapper newEntryWrapperInstanceFromJson(String requestBody, String entryId, String linkUri, ServiceHeaders headers);
}
