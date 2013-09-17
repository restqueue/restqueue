package com.restqueue.framework.client.entrywrappers;

import com.restqueue.common.utils.DateUtils;
import com.restqueue.common.utils.StringUtils;
import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ExpiryDate;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents the detail of a message on the channel. It holds the metadata for the message and the message content
 * itself.<BR/><BR/>
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

    public EntryWrapper() {
        long timeNow = System.currentTimeMillis();
        setCreated(timeNow);
        setLastUpdated(timeNow);
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public void setCreated(long created) {
        this.created = DateUtils.readableDate(created);
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = DateUtils.readableDate(lastUpdated);
    }

    public void setLinkUri(String linkUri) {
        this.linkUri = linkUri;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public void setMessageConsumerId(String messageConsumerId) {
        this.messageConsumerId = messageConsumerId;
    }

    public void addReturnAddress(ReturnAddress returnAddress) {
        if(returnAddresses==null){
            returnAddresses=new ArrayList<ReturnAddress>();
        }
        returnAddresses.add(returnAddress);
    }

    /**
     * This sets the delay String but if the delay-until has been set, use this, otherwise set the delay-until from the
     * delay header and created date
     * @param delay The delay String eg. "123" for 123 seconds OR "2m:3s" for 2 mins and 3 seconds
     */
    public void setDelay(String delay) {
        this.delay = delay;

        //if the delay until has been set, use this, otherwise set the delay until from the delay header and created date
        if(StringUtils.isNullOrEmpty(getDelayUntil())){
            if(StringUtils.isNotNullAndNotEmpty(this.delay)){
                setDelayUntil(ExpiryDate.fromDelayHeader(this.delay).toExpiryDateHeader(this.getCreated()));
            }
        }
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setDelayUntil(String delayUntil) {
        this.delayUntil = delayUntil;
    }

    public void setBatchKey(BatchKey batchKey) {
        this.batchKey = batchKey;
    }

    public void setPriority(int priority) {
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
