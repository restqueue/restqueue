package com.restqueue.framework.client.common.entryfields;

import com.restqueue.framework.service.exception.ChannelStoreException;

/**
 * This class defines how a message that is associated with a batch is identified.
 * <BR/><BR/>

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
 * Date: Feb 6, 2011
 * Time: 11:08:49 PM
 */
public class BatchKey {
    private String batchId;
    private int batchedMessageNumber;
    private int batchSize;

    private BatchKey() {
    }

    public BatchKey(String batchId, int batchedMessageNumber, int batchSize) {
        this.batchId = batchId;
        this.batchedMessageNumber = batchedMessageNumber;
        this.batchSize = batchSize;
    }

    public String getBatchId() {
        return batchId;
    }

    public int getBatchedMessageNumber() {
        return batchedMessageNumber;
    }

    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Creates a new BatchKey from the String representation. The String representation MUST be in the form
     * {BatchName}:{MessageNumber}/{BatchSize} eg. Batch1:1/2 meaning this message is in Batch1 and is message 1 of 2.
     *
     * @param batchKeyString The batch key representation
     * @return the new BatchKey
     */
    public static BatchKey parse(String batchKeyString){
        if(batchKeyString==null){
            return null;
        }

        if(!batchKeyString.contains(":")){
            throw new ChannelStoreException("Request has a malformed Batch key header - it MUST be in the form BatchName:MessageNumber/BatchSize " +
                    "eg. Batch1:1/2 meaning this message is in Batch1 and is message 1 of 2", ChannelStoreException.ExceptionType.INVALID_ENTRY_DATA_PROVIDED);
        }

        final BatchKey batchKey = new BatchKey();

        final String[] majorParts = batchKeyString.split(":");

        batchKey.batchId=majorParts[0];
        final String[] minorParts = majorParts[1].split("/");
        batchKey.batchedMessageNumber=Integer.parseInt(minorParts[0]);
        batchKey.batchSize=Integer.parseInt(minorParts[1]);

        return batchKey;
    }

    public String format(){
        return new StringBuilder(this.batchId).append(":").append(this.batchedMessageNumber).
                append("/").append(this.batchSize).toString();
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BatchKey batchKey = (BatchKey) o;

        if (batchedMessageNumber != batchKey.batchedMessageNumber) return false;
        if (batchId != null ? !batchId.equals(batchKey.batchId) : batchKey.batchId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = batchId != null ? batchId.hashCode() : 0;
        result = 31 * result + batchedMessageNumber;
        result = 31 * result + batchSize;
        return result;
    }
}
