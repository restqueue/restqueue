package com.restqueue.framework.client.common.serializer;

import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.exception.HttpResponseErrorBean;
import com.restqueue.framework.service.backingstorefilters.ArrivalOrderFilter;
import com.restqueue.framework.service.backingstorefilters.BatchingFilter;
import com.restqueue.framework.service.backingstorefilters.CompleteBatchFilter;
import com.restqueue.framework.service.backingstorefilters.SpecificPriorityFilter;
import com.restqueue.framework.service.channelstate.BatchStrategy;
import com.restqueue.framework.service.entrywrappers.EntrySummary;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.notification.MessageListenerAddress;
import com.restqueue.framework.service.notification.MessageListenerGroup;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

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
 * Time: 9:27:00 AM
 */
public class Serializer {
    private HTMLSerializer htmlSerializer = new HTMLSerializer();

    private Object fromJson(String json) {
        final XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        setupAliases(xstream);
        return xstream.fromXML(json);
    }

    private Object fromXml(String xml) {
        final XStream xStream = new XStream();
        setupAliases(xStream);
        return xStream.fromXML(xml);
    }

    public Object fromType(String content, String toType){
        if("application/xml".equals(toType)){
            return fromXml(content);
        }
        if("application/json".equals(toType)){
            return fromJson(content);
        }
        return null;
    }

    private String toXml(Object object) {
        final XStream xStream = new XStream();
        setupAliases(xStream);
        return xStream.toXML(object);
    }

    private String toJson(Object object) {
        final XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        setupAliases(xstream);
        return xstream.toXML(object);
    }

    private String toHTML(Object object, final String... arguments){
        return htmlSerializer.serialize(object, arguments);
    }

    public String toType(Object object, String toType, final String... arguments){
        if("application/xml".equals(toType)){
            return toXml(object);
        }
        if("application/json".equals(toType)){
            return toJson(object);
        }
        if("text/html".equals(toType)){
            return toHTML(object, arguments);
        }
        return null;
    }

    private void setupAliases(XStream xStream) {
        xStream.alias("entryWrapper", EntryWrapper.class);
        xStream.alias("entry", EntrySummary.class);
        xStream.alias("returnAddress", ReturnAddress.class);
        xStream.alias("error", HttpResponseErrorBean.class);
        xStream.alias("batchKey", BatchKey.class);
        xStream.alias("batchStrategy", BatchStrategy.class);
        xStream.alias("messageListenerGroup", MessageListenerGroup.class);
        xStream.alias("arrivalOrderFilter", ArrivalOrderFilter.class);
        xStream.alias("messageListenerAddress", MessageListenerAddress.class);
        xStream.alias("batchingFilter", BatchingFilter.class);
        xStream.alias("specificPriorityFilter", SpecificPriorityFilter.class);
        xStream.alias("completeBatchFilter", CompleteBatchFilter.class);
    }
}
