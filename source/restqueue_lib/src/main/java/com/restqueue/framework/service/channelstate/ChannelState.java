package com.restqueue.framework.service.channelstate;

import com.restqueue.common.utils.EnumUtils;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.service.exception.ChannelStoreException;

import java.util.*;

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
 * Time: 7:49:14 PM
 */
public class ChannelState {
    private Map<String, Object> map = new HashMap<String, Object>();
    private final Map<String,Boolean> immutabilityMap = new HashMap<String,Boolean>();
    private Map<String, Integer> defaultPriorityMap = new HashMap<String, Integer>();
    private List<String> currentBatchesList = new ArrayList<String>();

    public static final String SIZE_KEY = "size";
    public static final String MAX_SIZE_KEY = "maxSize";
    public static final String NEXT_MESSAGE_SEQUENCE_KEY = "nextMessageSequence";
    public static final String PRIORITY_MAP = "prioritySettings";
    public static final String CURRENT_BATCHES = "currentBatches";
    public static final String BATCH_STRATEGY = "batchStrategy";
    public static final String SEQUENCE_STRATEGY = "sequenceStrategy";

    public void initialize(){
        immutabilityMap.put(NEXT_MESSAGE_SEQUENCE_KEY,false);
        immutabilityMap.put(SIZE_KEY,true);
        immutabilityMap.put(MAX_SIZE_KEY,false);
        immutabilityMap.put(PRIORITY_MAP,true);
        immutabilityMap.put(CURRENT_BATCHES, true);
        immutabilityMap.put(BATCH_STRATEGY, false);
        immutabilityMap.put(SEQUENCE_STRATEGY, false);

        map.put(NEXT_MESSAGE_SEQUENCE_KEY, 0L);
        map.put(SIZE_KEY, 0);
        map.put(MAX_SIZE_KEY, 100);
        map.put(PRIORITY_MAP, defaultPriorityMap);
        map.put(CURRENT_BATCHES, currentBatchesList);
        map.put(BATCH_STRATEGY, BatchStrategy.ARRIVAL.name());
        map.put(SEQUENCE_STRATEGY, SequenceStrategy.GROUPED.name());

        defaultPriorityMap.put("high",66);
        defaultPriorityMap.put("medium",33);
        defaultPriorityMap.put("low",0);
    }

    public ChannelState() {
        initialize();
    }

    public Object put(String key, String value) {
        if(immutabilityMap.get(key)==null){
            throw new ChannelStoreException("Invalid state field:"+key,ChannelStoreException.ExceptionType.INVALID_STATE_FIELD);
        }
        if(immutabilityMap.get(key)){
            throw new ChannelStoreException("Cannot manually update the state field:"+key,ChannelStoreException.ExceptionType.READ_ONLY_STATE_FIELD);
        }

        if(BATCH_STRATEGY.equals(key)){
            try {
                return map.put(key, BatchStrategy.valueOf(value).name());
            } catch (IllegalArgumentException e) {
                throw new ChannelStoreException("Invalid state field value:"+value + " can be one of:"+ 
                        EnumUtils.stringArrayToGrammaticallyCorrectCommaList(BatchStrategy.values()),
                        ChannelStoreException.ExceptionType.INVALID_STATE_FIELD_VALUE);
            }
        }

        if(SEQUENCE_STRATEGY.equals(key)){
            try {
                return map.put(key, SequenceStrategy.valueOf(value).name());
            } catch (IllegalArgumentException e) {
                throw new ChannelStoreException("Invalid state field value:"+value + " can be one of:"+
                        EnumUtils.stringArrayToGrammaticallyCorrectCommaList(SequenceStrategy.values()),
                        ChannelStoreException.ExceptionType.INVALID_STATE_FIELD_VALUE);
            }
        }

        return map.put(key, convertToCorrectType(map.get(key).getClass(), value));
    }

    private Object convertToCorrectType(Class correctClass, String newValue) {
        if (Long.class.equals(correctClass)) {
            return Long.valueOf(newValue);
        }
        if (Integer.class.equals(correctClass)) {
            return Integer.valueOf(newValue);
        }
        if (Boolean.class.equals(correctClass)) {
            return Boolean.valueOf(newValue);
        }
        return newValue;
    }

    public String serializeToType(String asType) {
        if(asType.equals("text/html")){
            return new ChannelStateHtmlRenderer().serializeMap(map);
        }
        return new Serializer().toType(map, asType);
    }

    public static ChannelState fromMap(Map<String, Object> map){
        final ChannelState channelState = new ChannelState();
        channelState.map=map;
        return channelState;
    }

    public Object getFieldValue(String key) {
        if(immutabilityMap.get(key)==null){
            throw new ChannelStoreException("Invalid state field:"+key,ChannelStoreException.ExceptionType.INVALID_STATE_FIELD);
        }

        if(BATCH_STRATEGY.equals(key)){
            return map.get(key);
        }

        if(SEQUENCE_STRATEGY.equals(key)){
            return map.get(key);
        }
        return map.get(key);
    }

    @SuppressWarnings("unchecked")
    public Object getPriorityValue(String key) {
        if(((Map<String,Integer>)map.get(PRIORITY_MAP)).get(key)==null){
            throw new ChannelStoreException("Invalid priority:"+key,ChannelStoreException.ExceptionType.INVALID_PRIORITY);
        }
        return ((Map<String,Integer>)map.get(PRIORITY_MAP)).get(key);
    }

    public long getNextMessageSequence() {
        if (map.get(NEXT_MESSAGE_SEQUENCE_KEY) == null) {
            map.put(NEXT_MESSAGE_SEQUENCE_KEY, "0");
        }
        return (Long)(map.get(NEXT_MESSAGE_SEQUENCE_KEY));
    }

    public void refreshChannelState(long currentChannelSize, long currentChannelMaxSize){
        map.put(SIZE_KEY,currentChannelSize);
        map.put(MAX_SIZE_KEY,currentChannelMaxSize);
        getNextMessageSequence();
    }

    public String getETag() {
        return String.valueOf(serializeToType("application/json").hashCode());
    }

    public String getFieldETag(String stateField) {
        return String.valueOf((map.get(stateField)!=null?map.get(stateField):"").hashCode());
    }

    @SuppressWarnings("unchecked")
    public void putPriorityValue(String priority, String newValue) {
        ((Map<String,Integer>)map.get(PRIORITY_MAP)).put(priority, Integer.parseInt(newValue));
    }

    @SuppressWarnings("unchecked")
    public void removePriorityValue(String priority) {
        ((Map<String, Integer>) map.get(PRIORITY_MAP)).remove(priority);
    }

    @SuppressWarnings("unchecked")
    public void addBatchId(String batchId) {
        if(map.get(CURRENT_BATCHES)==null){
            map.put(CURRENT_BATCHES,new ArrayList<String>());
        }
        if(!((List<String>)map.get(CURRENT_BATCHES)).contains(batchId)){
            ((List<String>)map.get(CURRENT_BATCHES)).add(batchId);
        }
    }

    @SuppressWarnings("unchecked")
    public void removeBatchId(String batchId) {
        ((List<String>)map.get(CURRENT_BATCHES)).remove(batchId);
    }

    @SuppressWarnings("unchecked")
    public void clearBatchIds() {
        if (map.get(CURRENT_BATCHES) != null) {
            ((List<String>)map.get(CURRENT_BATCHES)).clear();
        }
    }

    @SuppressWarnings("unchecked")
    public static ChannelState defaultInstanceWithFieldsCopiedFrom(ChannelState channelState) {
        final ChannelState newChannelStateInstance = new ChannelState();

        if (channelState.getFieldValue(ChannelState.MAX_SIZE_KEY) != null) {
            newChannelStateInstance.put(ChannelState.MAX_SIZE_KEY, String.valueOf(channelState.getFieldValue(ChannelState.MAX_SIZE_KEY)));
        }
        if (channelState.getFieldValue(ChannelState.NEXT_MESSAGE_SEQUENCE_KEY) != null) {
            newChannelStateInstance.put(ChannelState.NEXT_MESSAGE_SEQUENCE_KEY, String.valueOf(channelState.getFieldValue(ChannelState.NEXT_MESSAGE_SEQUENCE_KEY)));
        }
        if (channelState.getFieldValue(ChannelState.BATCH_STRATEGY) != null) {
            newChannelStateInstance.put(ChannelState.BATCH_STRATEGY, (String) channelState.getFieldValue(ChannelState.BATCH_STRATEGY));
        }
        if (channelState.getFieldValue(ChannelState.SEQUENCE_STRATEGY) != null) {
            newChannelStateInstance.put(ChannelState.SEQUENCE_STRATEGY, (String) channelState.getFieldValue(ChannelState.SEQUENCE_STRATEGY));
        }

        if (channelState.getFieldValue(ChannelState.CURRENT_BATCHES) != null) {
            ((List<String>)newChannelStateInstance.getFieldValue(ChannelState.CURRENT_BATCHES)).clear();
            for (String currentBatch : (List<String>) channelState.getFieldValue(ChannelState.CURRENT_BATCHES)) {
                newChannelStateInstance.addBatchId(currentBatch);
            }
        }

        if (channelState.getFieldValue(ChannelState.PRIORITY_MAP) != null) {
            ((Map<String, Integer>)newChannelStateInstance.getFieldValue(ChannelState.PRIORITY_MAP)).clear();
            final Map<String, Integer> priorityMap = (Map<String, Integer>) channelState.getFieldValue(ChannelState.PRIORITY_MAP);
            for (Map.Entry<String, Integer> entry : priorityMap.entrySet()) {
                newChannelStateInstance.putPriorityValue(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        return newChannelStateInstance;
    }

}
