package com.restqueue.framework;

import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
 * Date: Jan 10, 2011
 * Time: 9:47:46 AM
 */
public class SerializerUnitTest {
    @Test
    public void serializerShouldCorrectlySerializeEntryWrapperToXml(){
        final String content = "Hello - Testing";
        final String creator = "Testing";
        final EntryWrapper entryWrapper = new EntryWrapper.EntryWrapperBuilder().setCreator(creator).setContent(content).setDelay("17").
                setPriority(11).setSequence(8).setBatchKey(new BatchKey("Batch_A",1,3)).buildNow();
        final String expectedRegexString = "<entryWrapper>\n" +
                "  <entryId></entryId>\n" +
                "  <created>.*</created>\n" +
                "  <creator>"+creator+"</creator>\n" +
                "  <lastUpdated>.*</lastUpdated>\n" +
                "  <linkUri></linkUri>\n" +
                "  <delay>.*</delay>\n" +
                "  <delayUntil>.*</delayUntil>\n" +
                "  <sequence>8</sequence>\n" +
                "  <priority>11</priority>\n" +
                "  <batchKey>\n" +
                "    <batchId>Batch_A</batchId>\n" +
                "    <batchedMessageNumber>1</batchedMessageNumber>\n" +
                "    <batchSize>3</batchSize>\n" +
                "  </batchKey>\n" +
                "  <content class=\"string\">"+content+"</content>\n" +
                "</entryWrapper>";
        
        final String result = new Serializer().toType(entryWrapper,"application/xml");
        assertTrue(result.matches(expectedRegexString));
    }

    @Test
    public void serializerShouldCorrectlySerializeEntryWrapperToJson(){
        final String content = "Hello - Testing";
        final String creator = "Testing";
        final EntryWrapper entryWrapper = new EntryWrapper.EntryWrapperBuilder().setCreator(creator).setContent(content).setDelay("17").setPriority(11).setSequence(8).buildNow();
        final String expectedRegexString = "\\{\"entryWrapper\":\\{\"entryId\":\"\",\"created\":\".*\",\"creator\":\"Testing\",\"" +
                "lastUpdated\":\".*\",\"linkUri\":\"\",\"delay\":17,\"delayUntil\":\".*\",\"sequence\":8,\"priority\":11,\"content\":\\{\"@class\":\"string\",\"\\$\":\"Hello - Testing\"\\}\\}\\}";
        final String result = new Serializer().toType(entryWrapper,"application/json");
        assertTrue(result.matches(expectedRegexString));
    }

    @Test
    public void serializerShouldCorrectlySerializeEntryWrapperWithReturnAddressesToJson(){
        final String content = "Hello - Testing";
        final String creator = "Testing";
        final EntryWrapper entryWrapper = new EntryWrapper.EntryWrapperBuilder().setCreator(creator).setContent(content).
                    addReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "me@there.com")).
                    addReturnAddress(new ReturnAddress(ReturnAddressType.URL, "http://localhost:9998/channels/1.0/stockQueryResultsQueue")).
                setDelay("17").setSequence(8).setPriority(11).buildNow();
        final String expectedRegexString = "\\{\"entryWrapper\":\\{\"entryId\":\"\",\"created\":\".*\",\"creator\":\"Testing\",\"" +
                "lastUpdated\":\".*\",\"linkUri\":\"\",\"delay\":17,\"delayUntil\":\".*\",\"sequence\":8,\"priority\":11," +
                "\"returnAddresses\":\\[\\{\"returnAddress\":\\[\\{\"type\":\"EMAIL\",\"address\":\"me@there.com\"\\},\\{\"type\":\"URL\"," +
                "\"address\":\"http:\\\\/\\\\/localhost:9998\\\\/channels\\\\/1.0\\\\/stockQueryResultsQueue\"\\}\\]\\}\\]," +
                "\"content\":\\{\"@class\":\"string\",\"\\$\":\"Hello - Testing\"\\}\\}\\}";
        final String result = new Serializer().toType(entryWrapper,"application/json");
        assertTrue(result.matches(expectedRegexString));
    }

    @Test
    public void toStringOfEntryWrapperShouldMatchAfterSerializingAndDeSerializing(){
        final EntryWrapper entryWrapperBefore = new EntryWrapper.EntryWrapperBuilder().setCreator("Testing").setContent("Hello - Testing").setDelay("60").
                    addReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "me@there.com")).
                    addReturnAddress(new ReturnAddress(ReturnAddressType.URL, "http://localhost:9998/channels/1.0/stockQueryResultsQueue")).buildNow();
        final String beforeString = ReflectionToStringBuilder.toString(entryWrapperBefore, ToStringStyle.SHORT_PREFIX_STYLE);

        final EntryWrapper entryWrapperAfter = (EntryWrapper)new Serializer().
                fromType(new Serializer().toType(entryWrapperBefore,"application/xml"),"application/xml");

        final String afterString = ReflectionToStringBuilder.toString(entryWrapperAfter, ToStringStyle.SHORT_PREFIX_STYLE);

        assertEquals(beforeString, afterString);
    }
}
