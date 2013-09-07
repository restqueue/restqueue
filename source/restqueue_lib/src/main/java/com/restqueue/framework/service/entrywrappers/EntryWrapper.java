package com.restqueue.framework.service.entrywrappers;

import com.restqueue.common.utils.DateUtils;
import com.restqueue.common.utils.StringUtils;
import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ExpiryDate;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.service.exception.SerializationException;
import com.restqueue.framework.service.transport.ServiceRequest;

import java.util.ArrayList;
import java.util.Arrays;
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
 * Date: Dec 19, 2010
 * Time: 6:59:58 PM
 */
public class EntryWrapper {
    private String entryId = "";
    private String created = "";
    private String creator = null;
    private String lastUpdated = "";
    private String linkUri = "";
    private String messageConsumerId = null;
    private String delay = "";
    private String delayUntil = "";
    private long sequence = -1;
    private int priority = 0;
    private List<ReturnAddress> returnAddresses = null;
    private BatchKey batchKey;
    private Object content = "";

    private void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    private void setCreated(long created) {
        this.created = DateUtils.readableDate(created);
    }

    private void setCreator(String creator) {
        this.creator = creator;
    }

    private void setLastUpdated(long lastUpdated) {
        this.lastUpdated = DateUtils.readableDate(lastUpdated);
    }

    private void setLinkUri(String linkUri) {
        this.linkUri = linkUri;
    }

    private void setContent(Object content) {
        this.content = content;
    }

    private void setMessageConsumerId(String messageConsumerId) {
        this.messageConsumerId = messageConsumerId;
    }

    private void addReturnAddress(ReturnAddress returnAddress) {
        if(returnAddresses==null){
            returnAddresses=new ArrayList<ReturnAddress>();
        }
        returnAddresses.add(returnAddress);
    }

    private void setDelay(String delay) {
        this.delay = delay;
    }

    private void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setDelayUntil(String delayUntil) {
        this.delayUntil = delayUntil;
    }

    private void setBatchKey(BatchKey batchKey) {
        this.batchKey = batchKey;
    }

    private void setPriority(int priority) {
        this.priority = priority;
    }

    public String getEntryId() {
        return entryId;
    }

    public String getCreated() {
        return created;
    }

    public String getCreator() {
        return creator;
    }

    public String getLastUpdated() {
        return lastUpdated;
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

    public String getETag() {
        return String.valueOf(hashCode());
    }

    public List<ReturnAddress> getReturnAddresses() {
        if(returnAddresses==null){
            return new ArrayList<ReturnAddress>();
        }
        return returnAddresses;
    }

    public String getDelay() {
        return delay;
    }

    public long getSequence() {
        return sequence;
    }

    public int getPriority() {
        return priority;
    }

    public BatchKey getBatchKey() {
        return batchKey;
    }

    public String getDelayUntil() {
        return delayUntil;
    }

    public static class EntryWrapperBuilder {
        private EntryWrapper entryWrapper = new EntryWrapper();

        public EntryWrapperBuilder setCreator(final String creator) {
            entryWrapper.setCreator(creator);
            return this;
        }

        public EntryWrapperBuilder setMessageConsumerId(final String messageConsumerId) {
            entryWrapper.setMessageConsumerId(messageConsumerId);
            return this;
        }

        public EntryWrapperBuilder setContent(final Object content) {
            entryWrapper.setContent(content);
            return this;
        }

        public EntryWrapperBuilder setSequence(final long sequence) {
            entryWrapper.setSequence(sequence);
            return this;
        }

        public EntryWrapperBuilder setPriority(final int priority) {
            entryWrapper.setPriority(priority);
            return this;
        }

        public EntryWrapperBuilder setDelay(final String delay) {
            entryWrapper.setDelay(delay);
            return this;
        }

        public EntryWrapperBuilder setDelayUntil(final String delayUntil) {
            entryWrapper.setDelayUntil(delayUntil);
            return this;
        }

        public EntryWrapperBuilder setCreated(final long created) {
            entryWrapper.setCreated(created);
            return this;
        }

        public EntryWrapperBuilder setLastUpdated(final long lastUpdated) {
            entryWrapper.setLastUpdated(lastUpdated);
            return this;
        }

        public EntryWrapperBuilder setLinkUri(final String linkUri) {
            entryWrapper.setLinkUri(linkUri);
            return this;
        }

        public EntryWrapperBuilder setEntryId(final String entryId) {
            entryWrapper.setEntryId(entryId);
            return this;
        }

        public EntryWrapperBuilder setBatchKey(final BatchKey batchKey) {
            entryWrapper.setBatchKey(batchKey);
            return this;
        }

        public EntryWrapperBuilder addReturnAddress(final ReturnAddress... returnAddresses){
            for(ReturnAddress returnAddress:returnAddresses){
                entryWrapper.addReturnAddress(returnAddress);
            }
            return this;
        }

        public EntryWrapper build() {
            return entryWrapper;
        }

        public EntryWrapper buildNow() {
            long timeNow = System.currentTimeMillis();
            entryWrapper.setCreated(timeNow);
            entryWrapper.setLastUpdated(timeNow);

            //if the delay until has been set, use this, otherwise set the delay until from the delay header and created date
            if(StringUtils.isNullOrEmpty(entryWrapper.getDelayUntil())){
                if(StringUtils.isNotNullAndNotEmpty(entryWrapper.delay)){
                    setDelayUntil(ExpiryDate.fromDelayHeader(entryWrapper.delay).toExpiryDateHeader(entryWrapper.getCreated()));
                }
            }

            return entryWrapper;
        }
    }

    public static class EntryWrapperFromXmlBuilder {
        private EntryWrapper entryWrapper= new EntryWrapper();

        public EntryWrapperFromXmlBuilder(final String xml) {
            entryWrapper.setContent(new Serializer().fromType(xml,"application/xml"));
        }

        public EntryWrapperFromXmlBuilder setCreator(final String creator) {
            entryWrapper.setCreator(creator);
            return this;
        }

        public EntryWrapperFromXmlBuilder setEntryId(final String entryId) {
            entryWrapper.setEntryId(entryId);
            return this;
        }

        public EntryWrapperFromXmlBuilder setSequence(final long sequence) {
            entryWrapper.setSequence(sequence);
            return this;
        }

        public EntryWrapperFromXmlBuilder setDelay(final String delay) {
            entryWrapper.setDelay(delay);
            return this;
        }

        public EntryWrapperFromXmlBuilder setDelayUntil(final String delayUntil) {
            entryWrapper.setDelayUntil(delayUntil);
            return this;
        }

        public EntryWrapperFromXmlBuilder setPriority(final int priority) {
            entryWrapper.setPriority(priority);
            return this;
        }

        public EntryWrapperFromXmlBuilder setLinkUri(final String linkUri) {
            entryWrapper.setLinkUri(linkUri);
            return this;
        }

        public EntryWrapperFromXmlBuilder setBatchKey(final BatchKey batchKey) {
            entryWrapper.setBatchKey(batchKey);
            return this;
        }

        public EntryWrapperFromXmlBuilder addReturnAddress(final ReturnAddress... returnAddresses){
            for(ReturnAddress returnAddress:returnAddresses){
                entryWrapper.addReturnAddress(returnAddress);
            }
            return this;
        }

        public EntryWrapper build() {
            long timeNow = System.currentTimeMillis();
            entryWrapper.setCreated(timeNow);
            entryWrapper.setLastUpdated(timeNow);

            //if the delay until has been set, use this, otherwise set the delay until from the delay header and created date
            if(StringUtils.isNullOrEmpty(entryWrapper.getDelayUntil())){
                if(StringUtils.isNotNullAndNotEmpty(entryWrapper.delay)){
                    setDelayUntil(ExpiryDate.fromDelayHeader(entryWrapper.delay).toExpiryDateHeader(entryWrapper.getCreated()));
                }
            }

            return entryWrapper;
        }
    }

    public static class EntryWrapperFromJsonBuilder {
        private EntryWrapper entryWrapper= new EntryWrapper();

        public EntryWrapperFromJsonBuilder(final String json) {
            entryWrapper.setContent(new Serializer().fromType(json,"application/json"));
        }

        public EntryWrapperFromJsonBuilder setCreator(final String creator) {
            entryWrapper.setCreator(creator);
            return this;
        }

        public EntryWrapperFromJsonBuilder setEntryId(final String entryId) {
            entryWrapper.setEntryId(entryId);
            return this;
        }

        public EntryWrapperFromJsonBuilder setSequence(final long sequence) {
            entryWrapper.setSequence(sequence);
            return this;
        }

        public EntryWrapperFromJsonBuilder setDelay(final String delay) {
            entryWrapper.setDelay(delay);
            return this;
        }

        public EntryWrapperFromJsonBuilder setDelayUntil(final String delayUntil) {
            entryWrapper.setDelayUntil(delayUntil);
            return this;
        }

        public EntryWrapperFromJsonBuilder setPriority(final int priority) {
            entryWrapper.setPriority(priority);
            return this;
        }

        public EntryWrapperFromJsonBuilder setLinkUri(final String linkUri) {
            entryWrapper.setLinkUri(linkUri);
            return this;
        }

        public EntryWrapperFromJsonBuilder setBatchKey(final BatchKey batchKey) {
            entryWrapper.setBatchKey(batchKey);
            return this;
        }

        public EntryWrapperFromJsonBuilder addReturnAddress(final ReturnAddress... returnAddresses){
            for(ReturnAddress returnAddress:returnAddresses){
                entryWrapper.addReturnAddress(returnAddress);
            }
            return this;
        }

        public EntryWrapper build() {
            long timeNow = System.currentTimeMillis();
            entryWrapper.setCreated(timeNow);
            entryWrapper.setLastUpdated(timeNow);

            //if the delay until has been set, use this, otherwise set the delay until from the delay header and created date
            if(StringUtils.isNullOrEmpty(entryWrapper.getDelayUntil())){
                if(StringUtils.isNotNullAndNotEmpty(entryWrapper.delay)){
                    setDelayUntil(ExpiryDate.fromDelayHeader(entryWrapper.delay).toExpiryDateHeader(entryWrapper.getCreated()));
                }
            }

            return entryWrapper;
        }
    }

    @Override
    public String toString() {
        return "EntryWrapper{" +
                "entryId='" + entryId + '\'' +
                ", created='" + created + '\'' +
                ", creator='" + creator + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", linkUri='" + linkUri + '\'' +
                ", messageConsumerId='" + messageConsumerId + '\'' +
                ", delayUntil=" + delayUntil +
                ", sequence=" + sequence +
                ", priority=" + priority +
                ", returnAddresses=" + returnAddresses +
                ", batchKey=" + batchKey +
                ", content=" + content +
                '}';
    }

    public void updateFromServiceRequest(final ServiceRequest serviceRequest) {
        if(serviceRequest.getBody()!=null && !serviceRequest.getBody().trim().equals("")){
            try {
                this.content = new Serializer().fromType(serviceRequest.getBody(), serviceRequest.getMediaTypeRequested());
            } catch (Exception e) {
                throw new SerializationException("Invalid content provided:"+serviceRequest.getBody(),e);
            }
        }

        if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_CONSUMER_ID)){
            this.messageConsumerId = serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_CONSUMER_ID);
        }

        if (serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.RETURN_ADDRESSES)) {
            this.returnAddresses = Arrays.asList(ReturnAddress.parse(serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.RETURN_ADDRESSES)));
        }
        if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_DELAY_UNTIL)){
            this.delayUntil=serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY_UNTIL);
        }
        if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_DELAY)){
            this.delay=serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_DELAY);
        }
        if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_SEQUENCE)){
            this.sequence=serviceRequest.getServiceHeaders().getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_SEQUENCE);
        }
        if(serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_PRIORITY)){
            this.priority=serviceRequest.getServiceHeaders().getSingleNullSafeIntHeaderValueFromHeaders(CustomHeaders.MESSAGE_PRIORITY);
        }
        if (serviceRequest.getServiceHeaders().hasHeaderValue(CustomHeaders.MESSAGE_BATCH_KEY)) {
            this.batchKey = BatchKey.parse(serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.MESSAGE_BATCH_KEY));
        }

        //if the delay until has been set, use this, otherwise set the delay until from the delay header and created date
        if (StringUtils.isNullOrEmpty(this.getDelayUntil())) {
            if (StringUtils.isNotNullAndNotEmpty(this.delay)) {
                setDelayUntil(ExpiryDate.fromDelayHeader(this.delay).toExpiryDateHeader(this.getCreated()));
            }
        }

        this.setLastUpdated(System.currentTimeMillis());        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntryWrapper that = (EntryWrapper) o;

        if (priority != that.priority) return false;
        if (sequence != that.sequence) return false;
        if (batchKey != null ? !batchKey.equals(that.batchKey) : that.batchKey != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (creator != null ? !creator.equals(that.creator) : that.creator != null) return false;
        if (entryId != null ? !entryId.equals(that.entryId) : that.entryId != null) return false;
        if (lastUpdated != null ? !lastUpdated.equals(that.lastUpdated) : that.lastUpdated != null) return false;
        if (linkUri != null ? !linkUri.equals(that.linkUri) : that.linkUri != null) return false;
        if (messageConsumerId != null ? !messageConsumerId.equals(that.messageConsumerId) : that.messageConsumerId != null)
            return false;
        if (returnAddresses != null ? !returnAddresses.equals(that.returnAddresses) : that.returnAddresses != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entryId != null ? entryId.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (lastUpdated != null ? lastUpdated.hashCode() : 0);
        result = 31 * result + (linkUri != null ? linkUri.hashCode() : 0);
        result = 31 * result + (messageConsumerId != null ? messageConsumerId.hashCode() : 0);
        result = 31 * result + (delayUntil != null ? delayUntil.hashCode() : 0);
        result = 31 * result + (int) (sequence ^ (sequence >>> 32));
        result = 31 * result + priority;
        result = 31 * result + (returnAddresses != null ? returnAddresses.toString().hashCode() : 0);
        result = 31 * result + (batchKey != null ? batchKey.toString().hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
