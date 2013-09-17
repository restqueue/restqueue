package com.restqueue.framework;

import com.restqueue.common.utils.DateUtils;
import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.entryfields.ReturnAddressType;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

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

        final EntryWrapper entryWrapper = new EntryWrapper();
        entryWrapper.setCreator(creator);
        entryWrapper.setContent(content);
        entryWrapper.setDelay("17");
        entryWrapper.setPriority(11);
        entryWrapper.setSequence(8);
        entryWrapper.setBatchKey(new BatchKey("Batch_A",1,3));

        final String readableDateNow = DateUtils.readableDate(System.currentTimeMillis());
        final String startOfDate = readableDateNow.substring(0,20);

        final String expectedRegexString = "<entryWrapper>\n" +
                "  <entryId></entryId>\n" +
                "  <created>"+startOfDate+".*</created>\n" +
                "  <creator>"+creator+"</creator>\n" +
                "  <lastUpdated>"+startOfDate+".*</lastUpdated>\n" +
                "  <linkUri></linkUri>\n" +
                "  <delay>17</delay>\n" +
                "  <delayUntil>"+startOfDate+".*</delayUntil>\n" +
                "  <sequence>8</sequence>\n" +
                "  <priority>11</priority>\n" +
                "  <batchKey>\n" +
                "    <batchId>Batch_A</batchId>\n" +
                "    <batchedMessageNumber>1</batchedMessageNumber>\n" +
                "    <batchSize>3</batchSize>\n" +
                "  </batchKey>\n" +
                "  <content class=\"string\">"+content+"</content>\n" +
                "</entryWrapper>";
        
        final String result = new Serializer().toType(entryWrapper,MediaType.APPLICATION_XML);
        assertTrue(result.matches(expectedRegexString));
    }

    @Test
    public void serializerShouldCorrectlySerializeEntryWrapperToJson(){
        final String content = "Hello - Testing";
        final String creator = "Testing";

        final EntryWrapper entryWrapper = new EntryWrapper();
        entryWrapper.setCreator(creator);
        entryWrapper.setContent(content);
        entryWrapper.setDelay("17");
        entryWrapper.setPriority(11);
        entryWrapper.setSequence(8);

        final String readableDateNow = DateUtils.readableDate(System.currentTimeMillis());
        final String startOfDate = readableDateNow.substring(0,20);

        final String expectedRegexString = "\\{\"entryWrapper\":\\{\"entryId\":\"\",\"created\":\""+startOfDate+".*\",\"creator\":\"Testing\",\"" +
                "lastUpdated\":\""+startOfDate+".*\",\"linkUri\":\"\",\"delay\":17,\"delayUntil\":\""+startOfDate+".*\",\"sequence\":8,\"priority\":11,\"content\":\\{\"@class\":\"string\",\"\\$\":\"Hello - Testing\"\\}\\}\\}";
        final String result = new Serializer().toType(entryWrapper, MediaType.APPLICATION_JSON);
        assertTrue(result.matches(expectedRegexString));
    }

    @Test
    public void serializerShouldCorrectlySerializeEntryWrapperWithReturnAddressesToJson(){
        final String content = "Hello - Testing";
        final String creator = "Testing";

        final EntryWrapper entryWrapper = new EntryWrapper();
        entryWrapper.setCreator(creator);
        entryWrapper.setContent(content);
        entryWrapper.addReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "me@there.com"));
        entryWrapper.addReturnAddress(new ReturnAddress(ReturnAddressType.URL, "http://localhost:9998/channels/1.0/stockQueryResultsQueue"));
        entryWrapper.setDelay("17");
        entryWrapper.setPriority(11);
        entryWrapper.setSequence(8);

        final String expectedRegexString = "\\{\"entryWrapper\":\\{\"entryId\":\"\",\"created\":\".*\",\"creator\":\"Testing\",\"" +
                "lastUpdated\":\".*\",\"linkUri\":\"\",\"delay\":17,\"delayUntil\":\".*\",\"sequence\":8,\"priority\":11," +
                "\"returnAddresses\":\\[\\{\"returnAddress\":\\[\\{\"type\":\"EMAIL\",\"address\":\"me@there.com\"\\},\\{\"type\":\"URL\"," +
                "\"address\":\"http:\\\\/\\\\/localhost:9998\\\\/channels\\\\/1.0\\\\/stockQueryResultsQueue\"\\}\\]\\}\\]," +
                "\"content\":\\{\"@class\":\"string\",\"\\$\":\"Hello - Testing\"\\}\\}\\}";
        final String result = new Serializer().toType(entryWrapper,MediaType.APPLICATION_JSON);
        assertTrue(result.matches(expectedRegexString));
    }

    @Test
    public void toStringOfEntryWrapperShouldMatchAfterSerializingAndDeSerializing(){
        final EntryWrapper entryWrapperBefore = new EntryWrapper();
        entryWrapperBefore.setCreator("Testing");
        entryWrapperBefore.setContent("Hello - Testing");
        entryWrapperBefore.addReturnAddress(new ReturnAddress(ReturnAddressType.EMAIL, "me@there.com"));
        entryWrapperBefore.addReturnAddress(new ReturnAddress(ReturnAddressType.URL, "http://localhost:9998/channels/1.0/stockQueryResultsQueue"));
        entryWrapperBefore.setDelay("60");
        entryWrapperBefore.setPriority(11);
        entryWrapperBefore.setSequence(8);

        final String beforeString = ReflectionToStringBuilder.toString(entryWrapperBefore, ToStringStyle.SHORT_PREFIX_STYLE);

        final EntryWrapper entryWrapperAfter = (EntryWrapper)new Serializer().
                fromType(new Serializer().toType(entryWrapperBefore,MediaType.APPLICATION_XML),MediaType.APPLICATION_XML);

        final String afterString = ReflectionToStringBuilder.toString(entryWrapperAfter, ToStringStyle.SHORT_PREFIX_STYLE);

        assertEquals(beforeString, afterString);
    }
}
