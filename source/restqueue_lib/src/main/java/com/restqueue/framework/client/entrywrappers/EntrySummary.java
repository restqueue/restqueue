package com.restqueue.framework.client.entrywrappers;

/**
 * This class represents a summary of the channel message. A serialised version of a list of these is what you get when
 * you query the channel contents. It simply wraps a location (which is an absolute URL to the message detail), the result
 * of the toString() method of the message class and the message consumer id for the message consumer that has this message
 * reserved or allocated to them.<BR/><BR/>
 *
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
    private String content = "";

    private EntrySummary() {
    }

    public String getLinkUri() {
        return linkUri;
    }

    public String getMessageConsumerId() {
        return messageConsumerId;
    }

    public String getContent() {
        return content;
    }

    public static EntrySummary fromEntryWrapper(EntryWrapper entryWrapper){
        final EntrySummary entrySummary = new EntrySummary();

        entrySummary.content=entryWrapper.getContent().toString();
        entrySummary.messageConsumerId=entryWrapper.getMessageConsumerId();
        entrySummary.linkUri=entryWrapper.getLinkUri();

        return entrySummary;
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
