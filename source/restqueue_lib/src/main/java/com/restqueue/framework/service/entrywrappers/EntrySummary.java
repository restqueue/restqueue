package com.restqueue.framework.service.entrywrappers;

import com.restqueue.framework.client.common.serializer.Serializer;

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
 * Date: Jan 16, 2011
 * Time: 9:14:02 PM
 */
public class EntrySummary {
    private String linkUri = "";
    private String messageConsumerId = null;
    private Object content = "";

    private EntrySummary() {
    }

    public String getLinkUri() {
        return linkUri;
    }

    public String getMessageConsumerId() {
        return messageConsumerId;
    }

    public Object getContent() {
        return content;
    }

    public static EntrySummary fromEntryWrapper(EntryWrapper entryWrapper){
        final EntrySummary entrySummary = new EntrySummary();

        entrySummary.content=entryWrapper.getContent();
        entrySummary.messageConsumerId=entryWrapper.getMessageConsumerId();
        entrySummary.linkUri=entryWrapper.getLinkUri();

        return entrySummary;
    }

    public String serializeToType(String asType) {
        return new Serializer().toType(this, asType);
    }

    @Override
    public String toString() {
        return "EntrySummary{" +
                "linkUri='" + linkUri + '\'' +
                ", messageConsumerId='" + messageConsumerId + '\'' +
                ", content=" + content +
                '}';
    }
}
