package com.restqueue.framework.service.backingstore;

import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.common.summaryfields.EndPoint;
import com.restqueue.framework.service.backingstoreduplicatesfilters.BackingStoreDuplicatesFilter;
import com.restqueue.framework.service.backingstorefilters.BackingStoreFilter;
import com.restqueue.framework.service.backingstorefilters.BatchingFilter;
import com.restqueue.framework.service.backingstorefilters.PriorityDescendingFilter;
import com.restqueue.framework.service.backingstorefilters.SpecificPriorityFilter;
import com.restqueue.framework.service.channelstate.ChannelState;
import com.restqueue.framework.service.entrywrappers.EntrySummary;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.exception.ChannelStoreException;
import com.restqueue.framework.service.exception.SerializationException;
import com.restqueue.framework.service.persistence.Persistence;
import com.restqueue.framework.service.persistence.PersistenceProvider;
import com.restqueue.framework.service.transport.ServiceRequest;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
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
 * Date: Dec 5, 2010
 * Time: 10:33:13 PM
 */
public final class ChannelBackingStore {
    private ChannelState channelState = new ChannelState();
    private BackingStoreDuplicatesFilter backingStoreDuplicatesFilter;
    private BackingStoreFilter backingStoreFilter;
    private BackingStoreFilter prioritizedStoreFilter=new PriorityDescendingFilter(new SpecificPriorityFilter());
    private BackingStoreFilter batchFilter = new BatchingFilter();
    private List<EntryWrapper> backingList = new ArrayList<EntryWrapper>();
    private int maxSize=-1;
    private Class associatedChannelResourceClazz;
    private long lastUpdated;

    private static final Logger log = Logger.getLogger(ChannelBackingStore.class);

    private Persistence persistence;

    private ChannelBackingStore(BackingStoreFilter backingStoreFilter, BackingStoreDuplicatesFilter backingStoreDuplicatesFilter,
                                Class associatedChannelResourceClazz) {
        this.backingStoreFilter = backingStoreFilter;
        this.backingStoreDuplicatesFilter = backingStoreDuplicatesFilter;
        this.associatedChannelResourceClazz = associatedChannelResourceClazz;

        persistence = PersistenceProvider.getPersistenceImplementationBasedOnProgramArguments();

        lastUpdated=System.currentTimeMillis();
    }

    public static ChannelBackingStore getBoundedInstance(BackingStoreFilter backingStoreFilter, BackingStoreDuplicatesFilter backingStoreDuplicatesFilter,
                                                         Class associatedChannelResourceClazz){
        return new ChannelBackingStore(backingStoreFilter, backingStoreDuplicatesFilter, associatedChannelResourceClazz);
    }

    public EntryWrapper add(EntryWrapper objectToAdd) {
        if(getMaxSize()>=0){
            if(getMaxSize()<=backingList.size()){
                throw new ChannelStoreException("Channel Backing Store must not exceed "+getMaxSize()+" items!",
                        ChannelStoreException.ExceptionType.CHANNEL_STORE_MAX_CAPACITY);
            }
        }

        final EntryWrapper entryWrapperAdded = backingStoreDuplicatesFilter.add(objectToAdd, backingList);

        if(objectToAdd.getBatchKey()!=null){
            channelState.addBatchId(objectToAdd.getBatchKey().getBatchId());
        }

        persist();

        lastUpdated=System.currentTimeMillis();

        return entryWrapperAdded;
    }

    public void remove(String entryWrapperId) {
        final EntryWrapper entryWrapperToRemove = getSpecificEntry(entryWrapperId);
        backingList.remove(entryWrapperToRemove);

        if(entryWrapperToRemove.getBatchKey()!=null){
            boolean removeBatchKeyFromCurrentBatchKeyList=true;
            for(EntryWrapper entryWrapper:backingList){
                if(entryWrapper.getBatchKey()!=null &&
                        entryWrapperToRemove.getBatchKey().getBatchId().equals(entryWrapper.getBatchKey().getBatchId())){
                    removeBatchKeyFromCurrentBatchKeyList=false;
                }
            }
            if(removeBatchKeyFromCurrentBatchKeyList){
                channelState.removeBatchId(entryWrapperToRemove.getBatchKey().getBatchId());
            }
        }

        lastUpdated=System.currentTimeMillis();

        persist();
    }

    public int getSize() {
        return backingList.size();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    private List<EntryWrapper> toList(){
        final List<EntryWrapper> list = new ArrayList<EntryWrapper>();

        for(EntryWrapper entry:backingList){
            list.add(entry);
        }

        return list;
    }

    private List<EntryWrapper> toAvailableList() {
        return backingStoreFilter.filter(backingList, this.channelState, new Object[0]);
    }

    private List<EntryWrapper> toPrioritizedAvailableList(String priority) {
        return prioritizedStoreFilter.filter(backingList, this.channelState, new Object[]{priority});
    }

    private List<EntryWrapper> toBatchedList(String batchId) {
        return batchFilter.filter(backingList, this.channelState, new Object[]{batchId});
    }

    public String serializeAvailableContentsAsSummariesToType(String asType) {
        final List<EntrySummary> entrySummaries=new ArrayList<EntrySummary>();
        for(EntryWrapper entryWrapper:toAvailableList()){
            entrySummaries.add(EntrySummary.fromEntryWrapper(entryWrapper));
        }
        return serializeContentsAsSummariesToType(entrySummaries, asType);
    }

    public String serializeSnapshotContentsAsSummariesToType(String snapshotId, String asType) {
        final List<EntrySummary> entrySummaries=new ArrayList<EntrySummary>();
        final List<EntryWrapper> entryWrappers=new ArrayList<EntryWrapper>();
        restoreContentsSnapshotIntoList(entryWrappers, snapshotId);
        for(EntryWrapper entryWrapper:entryWrappers){
            entrySummaries.add(EntrySummary.fromEntryWrapper(entryWrapper));
        }
        return serializeContentsAsSummariesToType(entrySummaries, asType, "snapshotContents");
    }

    private String serializeContentsAsSummariesToType(List<EntrySummary> entryWrapperList, String asType, String... arguments) {
        return new Serializer().toType(entryWrapperList,asType, arguments);
    }

    @SuppressWarnings("unchecked")
    public String getChannelSummaryObject(final String channelBaseUrl, final String asType){
        final List<EndPoint> endPoints = new ArrayList<EndPoint>();

        endPoints.add(new EndPoint.EndPointBuilder().setDescription("Channel Summary").
                setShortCode("CHANNEL_SUMMARY").setUrl(channelBaseUrl).build());

        endPoints.add(new EndPoint.EndPointBuilder().setDescription("Available Channel Contents").
                setShortCode("AVAILABLE_CHANNEL_CONTENTS").setUrl(channelBaseUrl + "/entries").build());

        endPoints.add(new EndPoint.EndPointBuilder().setDescription("Add Message To Channel").
                setShortCode("ADD_MESSAGE_TO_CHANNEL").setUrl(channelBaseUrl + "/entries").setHttpMethod(EndPoint.POST).
                setAccepts(EndPoint.DEFAULT_MEDIA_TYPES).build());

        endPoints.add(new EndPoint.EndPointBuilder().setDescription("Current Channel State").
                setShortCode("CURRENT_CHANNEL_STATE").setUrl(channelBaseUrl + "/state").build());

        endPoints.add(new EndPoint.EndPointBuilder().setDescription("Prioritised Available Channel Contents").
                setShortCode("AVAILABLE_PRIORITISED_CHANNEL_CONTENTS").setUrl(channelBaseUrl + "/entries/priority/all").build());

        final Map<String, Integer> priorityMap = (Map<String, Integer>) channelState.getFieldValue(ChannelState.PRIORITY_MAP);
        for (Map.Entry<String, Integer> entry : priorityMap.entrySet()) {
            endPoints.add(new EndPoint.EndPointBuilder().setDescription(entry.getKey() + " Priority Channel Contents").
                    setShortCode(entry.getKey().toUpperCase() + "_PRIORITY_CHANNEL_CONTENTS").
                    setUrl(channelBaseUrl + "/entries/priority/" + entry.getKey()).build());
        }
        
        final List<String> currentBatches = (List<String>)channelState.getFieldValue(ChannelState.CURRENT_BATCHES);
        for (String currentBatch : currentBatches) {
            endPoints.add(new EndPoint.EndPointBuilder().setDescription("Batch "+currentBatch + " Contents").
                    setShortCode("BATCH_"+currentBatch.toUpperCase() + "_CONTENTS").
                    setUrl(channelBaseUrl+"/entries/batch/"+currentBatch).build());
        }

        endPoints.add(new EndPoint.EndPointBuilder().setDescription("Channel Snapshots").
                setShortCode("CHANNEL_SNAPSHOTS").setUrl(channelBaseUrl + "/snapshots").build());

        endPoints.add(new EndPoint.EndPointBuilder().setDescription("Message Listeners").
                setShortCode("MESSAGE_LISTENERS").setUrl(channelBaseUrl + "/registration/messagelisteners").build());

        return new Serializer().toType(endPoints, asType);
    }

    public void restoreFromPersistedState(){
        restoreContentsIntoList(backingList);
        restoreState();
    }

    private void restoreState(){
        channelState = persistence.loadChannelState(associatedChannelResourceClazz);
        setMaxSize((Integer) channelState.getFieldValue(ChannelState.MAX_SIZE_KEY));
    }        

    private void restoreContentsIntoList(final List<EntryWrapper> listToRestoreInto){
        listToRestoreInto.clear();
        listToRestoreInto.addAll(persistence.loadChannelContents(associatedChannelResourceClazz));
    }

    private void restoreContentsSnapshotIntoList(final List<EntryWrapper> listToRestoreInto, String snapshotId){
        listToRestoreInto.clear();
        listToRestoreInto.addAll(persistence.loadChannelContentsSnapshot(associatedChannelResourceClazz, snapshotId));
    }

    public void persist(){
        updateChannelState();
        final HashMap<String, Object> changesMap = new HashMap<String, Object>();
        changesMap.put(Persistence.CHANNEL_CONTENTS_KEY, toList());
        changesMap.put(Persistence.CHANNEL_STATE_KEY, channelState);
        persistence.saveUpdated(associatedChannelResourceClazz, changesMap);
    }

    public String takeSnapshot(){
        final String snapshotId = new SimpleDateFormat(persistence.getDateFormatForSnapshotId()).format(new Date());
        updateChannelState();
        persistence.takeChannelSnapshot(associatedChannelResourceClazz, toList(), channelState, snapshotId);
        return snapshotId;
    }

    public String purge(){
        takeSnapshot();
        backingList.clear();
        persist();

        updateChannelState();

        log.info("Channel contents for "+associatedChannelResourceClazz.getCanonicalName()+" purged successfully.");

        return "Channel contents purged successfully.";
    }

    public EntryWrapper getSpecificEntry(final String entryId) {
        EntryWrapper entryWrapperToReturn=null;
        for(EntryWrapper entryWrapper: toList()){
            if(entryWrapper.getEntryId().equals(entryId)){
                entryWrapperToReturn=entryWrapper;
                break;
            }
        }
        if(entryWrapperToReturn==null){
            throw new ChannelStoreException("Specified entry not found:"+entryId, ChannelStoreException.ExceptionType.ENTRY_NOT_FOUND);
        }
        return entryWrapperToReturn;
    }

    public void updateSpecifiedEntryFromType(final EntryWrapper entryWrapperToUpdate, final ServiceRequest serviceRequest){
        try {
            backingStoreDuplicatesFilter.updateFromServiceRequest(entryWrapperToUpdate, serviceRequest, backingList);

            lastUpdated=System.currentTimeMillis();

        } catch (SerializationException e) {
            log.error(e.getMessage(), e);
            throw new ChannelStoreException(e.getMessage(),ChannelStoreException.ExceptionType.INVALID_ENTRY_DATA_PROVIDED);
        }
    }

    public void updateChannelState(){
        channelState.refreshChannelState(getSize(), getMaxSize());
        updateCurrentBatchIds();
    }

    public void updateCurrentBatchIds() {
        channelState.clearBatchIds();
        for (EntryWrapper entryWrapper : backingList) {
            if (entryWrapper.getBatchKey() != null) {
                channelState.addBatchId(entryWrapper.getBatchKey().getBatchId());
            }
        }
    }

    public String serializeChannelStateToType(String asType){
        return channelState.serializeToType(asType);
    }

    public String getChannelStateETag() {
        return channelState.getETag();
    }

    public String getChannelStateFieldETag(String stateField) {
        return channelState.getFieldETag(stateField);
    }

    public Object getChannelStateFieldValue(String stateField) {
        return channelState.getFieldValue(stateField);
    }

    public void putChannelStateFieldValue(String stateField, String newValue) {
        channelState.put(stateField, newValue);
        if(ChannelState.MAX_SIZE_KEY.equals(stateField)){
            setMaxSize(Integer.valueOf(newValue));
        }
        final HashMap<String, Object> changesMap = new HashMap<String, Object>();
        changesMap.put(Persistence.CHANNEL_STATE_KEY, channelState);
        persistence.saveUpdated(associatedChannelResourceClazz, changesMap);
    }

    public Object getChannelPriorityFieldValue(String priority) {
        return channelState.getPriorityValue(priority);
    }

    public void putChannelPriorityFieldValue(String priority, String newValue) {
        channelState.putPriorityValue(priority, newValue);
        final HashMap<String, Object> changesMap = new HashMap<String, Object>();
        changesMap.put(Persistence.CHANNEL_STATE_KEY, channelState);
        persistence.saveUpdated(associatedChannelResourceClazz, changesMap);
    }

    public void removeChannelPriorityFieldValue(String priority) {
        channelState.removePriorityValue(priority);
        final HashMap<String, Object> changesMap = new HashMap<String, Object>();
        changesMap.put(Persistence.CHANNEL_STATE_KEY, channelState);
        persistence.saveUpdated(associatedChannelResourceClazz, changesMap);
    }

    public String serializePrioritizedAvailableContentsAsSummariesToType(String priority, String asType) {
        final List<EntrySummary> entrySummaries=new ArrayList<EntrySummary>();
        for(EntryWrapper entryWrapper:toPrioritizedAvailableList(priority)){
            entrySummaries.add(EntrySummary.fromEntryWrapper(entryWrapper));
        }
        return serializeContentsAsSummariesToType(entrySummaries, asType);
    }

    public String serializeBatchedAvailableContentsAsSummariesToType(String batchId, String asType) {
        final List<EntrySummary> entrySummaries=new ArrayList<EntrySummary>();
        for(EntryWrapper entryWrapper: toBatchedList(batchId)){
            entrySummaries.add(EntrySummary.fromEntryWrapper(entryWrapper));
        }
        return serializeContentsAsSummariesToType(entrySummaries, asType);
    }

    public List<String> serializeSnapshotListToType() {
        return persistence.getSnapshotList(associatedChannelResourceClazz);
    }

    public String restoreFromSnapshot(final String snapshotId) {
        persistence.overwriteCurrentDataWithSnapshot(associatedChannelResourceClazz, snapshotId);

        //force a restore from working directory
        restoreFromPersistedState();

        final String message = "Successfully restored contents from snapshot";
        log.info(message);

        lastUpdated=System.currentTimeMillis();

        return message;
    }

    public ChannelState getChannelState() {
        return channelState;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
