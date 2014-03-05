package com.restqueue.framework.service.resourcedelegate;

import com.restqueue.common.arguments.ServerArguments;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.service.backingstore.ChannelBackingStore;
import com.restqueue.framework.service.entrywrapperfactories.EntryWrapperFactory;
import com.restqueue.framework.service.entrywrapperfactories.EntryWrapperFactoryImpl;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.exception.ChannelStoreException;
import com.restqueue.framework.service.exception.ExceptionRenderer;
import com.restqueue.framework.service.notification.*;
import com.restqueue.framework.service.server.AbstractServer;
import com.restqueue.framework.service.transport.ServiceHeaders;
import com.restqueue.framework.service.transport.ServiceRequest;
import com.restqueue.framework.service.transport.ServiceResponse;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Date: Jan 4, 2011
 * Time: 7:31:30 PM
 */

public class ChannelResourceDelegate {
    private ChannelBackingStore backingStore;
    private MessageListenerNotification messageListenerNotification;
    private EntryWrapperFactory entryWrapperFactory = new EntryWrapperFactoryImpl();

    protected static final Logger log = Logger.getLogger(ChannelResourceDelegate.class);
    private final String channelEndPoint;

    public ChannelResourceDelegate(ChannelBackingStore backingStore, final String channelEndPoint,
                                   MessageListenerNotification messageListenerNotification) {
        this.backingStore = backingStore;
        this.channelEndPoint = channelEndPoint;
        this.messageListenerNotification = messageListenerNotification;
    }

    public ServiceResponse getChannelSummaryAsType(final ServiceRequest serviceRequest) {
        final String contentsString = backingStore.getChannelSummaryObject("http://"+localIpAddress()+":"+ AbstractServer.PORT+channelEndPoint,
                "http://"+localIpAddress()+":"+ AbstractServer.PORT,
                serviceRequest.getMediaTypeRequested());
        return new ServiceResponse.ServiceResponseBuilder().setBody(contentsString).setReturnCode(200).build();
    }

    public ServiceResponse getChannelContentsAsType(final ServiceRequest serviceRequest) {
        //check last updated date to enable not-modified response (with empty body)
        if (!ServerArguments.getInstance().getBooleanArgument(AbstractServer.NO_CACHE_SWITCH) &&
                serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_NONE_MATCH) != null &&
                serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_NONE_MATCH).size() != 0 &&
                !backingStore.justRevealedNewMessages() &&
                Long.valueOf(serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_NONE_MATCH).get(0)).equals(backingStore.getLastUpdated())) {
            final ServiceHeaders headers = new ServiceHeaders.ServiceHeadersBuilder().
                    addHeader(CustomHeaders.ETAG, backingStore.getLastUpdated()).
                    addHeader(CustomHeaders.CACHE_CONTROL, "no-cache").build();
            log.info("Channel contents are unchanged and a conditional GET request was received - returning http code 304 (Unmodified).");
            return new ServiceResponse.ServiceResponseBuilder().setServiceHeaders(headers).setReturnCode(304).build();
        }

        final String contentsString = backingStore.serializeAvailableContentsAsSummariesToType(serviceRequest.getMediaTypeRequested());
        final ServiceHeaders headers = new ServiceHeaders.ServiceHeadersBuilder().addHeader(CustomHeaders.ETAG, backingStore.getLastUpdated()).build();
        return new ServiceResponse.ServiceResponseBuilder().setServiceHeaders(headers).setBody(contentsString).setReturnCode(200).build();
    }

    public ServiceResponse addMessageToChannelAsType(final ServiceRequest serviceRequest) throws URISyntaxException {
        log.info("Attempting to add message:" + serviceRequest.getBody() + " to channel:" + channelEndPoint);
        final String entryId = String.valueOf(System.currentTimeMillis());
        final String linkUri = new StringBuilder().append("http://").append(localIpAddress()).append(":").
                append(AbstractServer.PORT).append(channelEndPoint).append("/entries/").append(entryId).toString();

        final EntryWrapper entryWrapper;
        EntryWrapper entryWrapperAdded = null;
        try {

            if (MediaType.APPLICATION_XML.equals(serviceRequest.getMediaTypeRequested())) {
                entryWrapper = entryWrapperFactory.newEntryWrapperInstanceFromXml(serviceRequest.getBody(), entryId, linkUri, serviceRequest.getServiceHeaders());
            }
            else if (MediaType.APPLICATION_JSON.equals(serviceRequest.getMediaTypeRequested())) {
                entryWrapper = entryWrapperFactory.newEntryWrapperInstanceFromJson(serviceRequest.getBody(), entryId, linkUri, serviceRequest.getServiceHeaders());
            }
            else {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(415).build();
            }

            entryWrapperAdded = backingStore.add(entryWrapper);
        }
        catch (ChannelStoreException cse) {
            log.warn("Exception adding message:" + serviceRequest.getBody() + " to channel:" + channelEndPoint + " - " + cse.getMessage());
            if (ChannelStoreException.ExceptionType.CHANNEL_STORE_MAX_CAPACITY.equals(cse.getExceptionType()) ||
                    ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED.equals(cse.getExceptionType())) {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(409).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
            if(ChannelStoreException.ExceptionType.INVALID_ENTRY_DATA_PROVIDED.equals(cse.getExceptionType())){
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(400).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
            else{
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(500).setBody(
                                        ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        }
        backingStore.updateChannelState();

        AsynchronousNotification.getInstance().requestNotification(
                new NotificationRequest(messageListenerNotification, entryWrapperAdded, backingStore.getChannelState()));

        final ServiceHeaders headers = new ServiceHeaders.ServiceHeadersBuilder().
                addHeader(CustomHeaders.LOCATION, new URI(entryWrapperAdded.getLinkUri()).toString()).
                addHeader(CustomHeaders.ETAG, entryWrapperAdded.getETag()).build();
        log.info("Successfully added message:" + serviceRequest.getBody() + " to channel:" + channelEndPoint);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(201).setServiceHeaders(headers).build();
    }

    private String localIpAddress() {
        String ipAddress;
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ipAddress = "localhost";
        }
        return ipAddress;
    }

    public ServiceResponse getSpecificEntryAsType(final ServiceRequest serviceRequest) {
        EntryWrapper entryWrapperToReturn = null;
        try {
            entryWrapperToReturn = backingStore.getSpecificEntry(serviceRequest.getParameter("entryId"));
        } catch (ChannelStoreException cse) {
            if (ChannelStoreException.ExceptionType.ENTRY_NOT_FOUND.equals(cse.getExceptionType())) {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(404).setBody(
                        ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
            else{
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(500).setBody(
                                        ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        }
        final ServiceHeaders.ServiceHeadersBuilder builder = new ServiceHeaders.ServiceHeadersBuilder().addHeader(CustomHeaders.ETAG, entryWrapperToReturn.getETag()).
                addHeader(CustomHeaders.CREATED_DATE, entryWrapperToReturn.getCreated()).addHeader(CustomHeaders.CREATOR, entryWrapperToReturn.getCreator()).
                addHeader(CustomHeaders.LAST_MODIFIED, entryWrapperToReturn.getLastUpdated()).addHeader(CustomHeaders.LOCATION, entryWrapperToReturn.getLinkUri()).
                addHeader(CustomHeaders.MESSAGE_CONSUMER_ID, entryWrapperToReturn.getMessageConsumerId()).
                addHeader(CustomHeaders.MESSAGE_SEQUENCE, entryWrapperToReturn.getSequence()).
                addHeader(CustomHeaders.MESSAGE_PRIORITY, entryWrapperToReturn.getPriority()).
                addHeader(CustomHeaders.MESSAGE_DELAY_UNTIL, entryWrapperToReturn.getDelayUntil());

        for(ReturnAddress returnAddress:entryWrapperToReturn.getReturnAddresses()){
            builder.addHeader(CustomHeaders.RETURN_ADDRESSES,returnAddress.format());
        }

        if(entryWrapperToReturn.getBatchKey()!=null){
            builder.addHeader(CustomHeaders.MESSAGE_BATCH_KEY, entryWrapperToReturn.getBatchKey().format());
        }

        final ServiceHeaders serviceHeaders = builder.build();
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).
                setBody(new Serializer().toType(entryWrapperToReturn.getContent(),serviceRequest.getMediaTypeRequested())).
                setServiceHeaders(serviceHeaders).build();
    }

    public ServiceResponse updateSpecificEntryAsType(final ServiceRequest serviceRequest) {
        log.info("Attempting to update message:"+ serviceRequest.getParameter("entryId") +" in channel:"+channelEndPoint);
        EntryWrapper entryWrapperToReturn = null;
        try {
            entryWrapperToReturn = backingStore.getSpecificEntry(serviceRequest.getParameter("entryId"));
        } catch (ChannelStoreException cse) {
            log.warn("Exception updating message:"+serviceRequest.getParameter("entryId")+" in channel:"+channelEndPoint+" - "+cse.getMessage());
            if (ChannelStoreException.ExceptionType.ENTRY_NOT_FOUND.equals(cse.getExceptionType())) {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(404).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        }
        if (serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH) == null ||
                serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH).size() == 0 ||
                !serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.IF_MATCH).equals(entryWrapperToReturn.getETag())) {

            final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().addHeader(CustomHeaders.ETAG, entryWrapperToReturn.getETag()).build();
            log.info("Precondition failed for changing message:"+serviceRequest.getParameter("entryId") +" in channel:"+channelEndPoint+", expected "+
                    entryWrapperToReturn.getETag()+", provided:"+
                    serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH));
            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(412).
                    setBody(new Serializer().toType(entryWrapperToReturn.getContent(),serviceRequest.getMediaTypeRequested())).setServiceHeaders(serviceHeaders).build();
        }

        try {
            backingStore.updateSpecifiedEntryFromType(entryWrapperToReturn, serviceRequest);
        } catch (ChannelStoreException cse) {
            log.warn("Exception updating message:"+serviceRequest.getParameter("entryId")+" in channel:"+channelEndPoint+" - "+cse.getMessage());
            if (ChannelStoreException.ExceptionType.INVALID_ENTRY_DATA_PROVIDED.equals(cse.getExceptionType())) {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(400).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
            if (ChannelStoreException.ExceptionType.DUPLICATE_MESSAGE_DATA_NOT_ALLOWED.equals(cse.getExceptionType())) {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(409).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        } catch (NumberFormatException nfe) {
            log.warn("Exception updating message:"+serviceRequest.getParameter("entryId")+" in channel:"+channelEndPoint+" - "+nfe.getMessage());
            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(400).
                    setBody(ExceptionRenderer.renderExceptionAsType("Invalid numeric content: "+nfe.getMessage(), serviceRequest.getMediaTypeRequested())).build();
        }
        backingStore.persist();

        final ServiceHeaders.ServiceHeadersBuilder builder = new ServiceHeaders.ServiceHeadersBuilder().addHeader(CustomHeaders.ETAG, entryWrapperToReturn.getETag()).
                addHeader(CustomHeaders.CREATED_DATE, entryWrapperToReturn.getCreated()).addHeader(CustomHeaders.CREATOR, entryWrapperToReturn.getCreator()).
                addHeader(CustomHeaders.LAST_MODIFIED, entryWrapperToReturn.getLastUpdated()).addHeader(CustomHeaders.LOCATION, entryWrapperToReturn.getLinkUri()).
                addHeader(CustomHeaders.MESSAGE_CONSUMER_ID, entryWrapperToReturn.getMessageConsumerId()).
                addHeader(CustomHeaders.MESSAGE_SEQUENCE, entryWrapperToReturn.getSequence()).
                addHeader(CustomHeaders.MESSAGE_PRIORITY, entryWrapperToReturn.getPriority()).
                addHeader(CustomHeaders.MESSAGE_DELAY_UNTIL, entryWrapperToReturn.getDelayUntil());

        for(ReturnAddress returnAddress:entryWrapperToReturn.getReturnAddresses()){
            builder.addHeader(CustomHeaders.RETURN_ADDRESSES,returnAddress.format());
        }

        if(entryWrapperToReturn.getBatchKey()!=null){
            builder.addHeader(CustomHeaders.MESSAGE_BATCH_KEY, entryWrapperToReturn.getBatchKey().format());
        }

        final ServiceHeaders serviceHeaders = builder.build();

        log.info("Successfully updated message:"+ serviceRequest.getParameter("entryId") +" in channel:"+channelEndPoint);
        return new ServiceResponse.ServiceResponseBuilder().setBody(new Serializer().toType(entryWrapperToReturn.getContent(),serviceRequest.getMediaTypeRequested())).
                setReturnCode(200).setServiceHeaders(serviceHeaders).build();
    }

    public ServiceResponse getChannelStateAsType(final ServiceRequest serviceRequest) {
        backingStore.updateChannelState();
        final String contentsString = backingStore.serializeChannelStateToType(serviceRequest.getMediaTypeRequested());
        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().addHeader(CustomHeaders.ETAG, backingStore.getChannelStateETag()).build();
        return new ServiceResponse.ServiceResponseBuilder().setBody(contentsString).setReturnCode(200).setServiceHeaders(serviceHeaders).build();
    }

    public ServiceResponse getChannelStateField(final ServiceRequest serviceRequest) {
        Object valueOfStateField = null;
        try {
            valueOfStateField = backingStore.getChannelStateFieldValue(serviceRequest.getParameter("stateField"));
        } catch (ChannelStoreException cse) {
            if (ChannelStoreException.ExceptionType.INVALID_STATE_FIELD.equals(cse.getExceptionType())) {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(404).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        }

        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                addHeader(CustomHeaders.ETAG, backingStore.getChannelStateFieldETag(serviceRequest.getParameter("stateField"))).build();
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).setBody(String.valueOf(valueOfStateField)).
                setServiceHeaders(serviceHeaders).build();
    }

    public ServiceResponse updateSpecificStateField(final ServiceRequest serviceRequest) {
        log.info("Attempting to update state field:"+ serviceRequest.getParameter("stateField") +" to value:"+serviceRequest.getBody()+" in channel:"+channelEndPoint);
        try {
            if (serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH) == null ||
                    serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH).size() == 0 ||
                    !serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.IF_MATCH).equals(
                            backingStore.getChannelStateFieldETag(serviceRequest.getParameter("stateField")))) {
                final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                        addHeader(CustomHeaders.ETAG, backingStore.getChannelStateFieldETag(serviceRequest.getParameter("stateField"))).build();
                log.info("Precondition failed for changing state field:"+serviceRequest.getParameter("stateField")+", expected "+
                        backingStore.getChannelStateFieldETag(serviceRequest.getParameter("stateField"))+", provided:"+
                        serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.IF_MATCH));
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(412).
                        setBody(String.valueOf(backingStore.getChannelStateFieldValue(serviceRequest.getParameter("stateField")))).
                        setServiceHeaders(serviceHeaders).build();
            }
            backingStore.putChannelStateFieldValue(serviceRequest.getParameter("stateField"), serviceRequest.getBody());

            final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                    addHeader(CustomHeaders.ETAG, backingStore.getChannelStateFieldETag(serviceRequest.getParameter("stateField"))).build();
            log.info("Successfully updated state field:"+ serviceRequest.getParameter("stateField") +
                    " to value:"+serviceRequest.getBody()+" in channel:"+channelEndPoint);
            backingStore.persist();
            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).
                    setBody(String.valueOf(backingStore.getChannelStateFieldValue(serviceRequest.getParameter("stateField")))).
                    setServiceHeaders(serviceHeaders).build();

        } catch (ChannelStoreException cse) {
            log.warn("Exception updating state field:"+serviceRequest.getParameter("stateField")+" in channel:"+channelEndPoint+" - "+cse.getMessage());
            if (ChannelStoreException.ExceptionType.INVALID_STATE_FIELD.equals(cse.getExceptionType()) ||
                    ChannelStoreException.ExceptionType.READ_ONLY_STATE_FIELD.equals(cse.getExceptionType()) ||
                    ChannelStoreException.ExceptionType.INVALID_STATE_FIELD_VALUE.equals(cse.getExceptionType())) {
                log.warn(cse.getMessage());

                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(403).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        } catch (NumberFormatException nfe) {
            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(400).
                    setBody(ExceptionRenderer.renderExceptionAsType("Invalid numeric content: "+nfe.getMessage(), serviceRequest.getMediaTypeRequested())).build();
        }

        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(500).build();
    }

    public ServiceResponse removeSpecificEntry(final ServiceRequest serviceRequest) {
        log.info("Attempting to delete message:"+ serviceRequest.getParameter("entryId") +" from channel:"+channelEndPoint);

        try {
            backingStore.remove(serviceRequest.getParameter("entryId"));
        } catch (ChannelStoreException cse) {
            //do nothing if the entry does not exist so that the delete is idempotent
            log.info("Exception when deleting message:"+serviceRequest.getParameter("entryId")+" - "+cse.getMessage());
        }
        log.info("Successfully deleted message:"+ serviceRequest.getParameter("entryId") +" from channel:"+channelEndPoint);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).build();
    }

    public ServiceResponse getPriorityField(final ServiceRequest serviceRequest) {
        Object valueOfPriorityField = null;
        try {
            valueOfPriorityField = backingStore.getChannelPriorityFieldValue(serviceRequest.getParameter("priority"));
        } catch (ChannelStoreException cse) {
            if (ChannelStoreException.ExceptionType.INVALID_PRIORITY.equals(cse.getExceptionType())) {
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(404).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        }

        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                addHeader(CustomHeaders.ETAG, String.valueOf(valueOfPriorityField)).build();
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).setBody(String.valueOf(valueOfPriorityField)).
                setServiceHeaders(serviceHeaders).build();
    }

    public ServiceResponse addOrUpdateSpecificPriority(ServiceRequest serviceRequest) {
        try {

            Object priorityValue;
            try {
                priorityValue = backingStore.getChannelPriorityFieldValue(serviceRequest.getParameter("priority"));

                log.info("Attempting to update priority:"+ serviceRequest.getParameter("priority") +" to value:"+serviceRequest.getBody()+" in channel:"+channelEndPoint);

                if (serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH) == null ||
                        serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH).size() == 0 ||
                        !serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.IF_MATCH).equals(
                                String.valueOf(priorityValue))) {
                    final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                            addHeader(CustomHeaders.ETAG, String.valueOf(priorityValue)).build();
                    log.info("Precondition failed for changing priority expected "+
                            String.valueOf(priorityValue)+", provided:"+
                            serviceRequest.getServiceHeaders().getHeaderValueList(CustomHeaders.IF_MATCH));
                    return new ServiceResponse.ServiceResponseBuilder().setReturnCode(412).
                            setBody(String.valueOf(priorityValue)).
                            setServiceHeaders(serviceHeaders).build();
                }
            } catch (ChannelStoreException cse) {
                //do nothing - add rather than replace value
                log.info("Attempting to add priority:"+ serviceRequest.getParameter("priority") +" to value:"+serviceRequest.getBody()+" in channel:"+channelEndPoint);
            }
            backingStore.putChannelPriorityFieldValue(serviceRequest.getParameter("priority"), serviceRequest.getBody());

            priorityValue = backingStore.getChannelPriorityFieldValue(serviceRequest.getParameter("priority"));

            final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                    addHeader(CustomHeaders.ETAG, String.valueOf(priorityValue)).build();

            log.info("Successfully updated priority:"+ serviceRequest.getParameter("priority") +" to value:"+serviceRequest.getBody()+" in channel:"+channelEndPoint);

            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).
                    setBody(String.valueOf(priorityValue)).
                    setServiceHeaders(serviceHeaders).build();

        } catch (ChannelStoreException cse) {
            log.warn("Exception updating priority:"+serviceRequest.getParameter("priority")+" in channel:"+channelEndPoint+" - "+cse.getMessage());
            if (ChannelStoreException.ExceptionType.INVALID_PRIORITY.equals(cse.getExceptionType()) ||
                    ChannelStoreException.ExceptionType.READ_ONLY_STATE_FIELD.equals(cse.getExceptionType())) {
                log.warn(cse.getMessage());

                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(403).
                        setBody(ExceptionRenderer.renderExceptionAsType(cse, serviceRequest.getMediaTypeRequested())).build();
            }
        } catch (NumberFormatException nfe) {
            log.warn("Exception updating priority:"+serviceRequest.getParameter("priority")+" in channel:"+channelEndPoint+" - "+nfe.getMessage());
            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(400).
                    setBody(ExceptionRenderer.renderExceptionAsType("Invalid numeric content: "+nfe.getMessage(), serviceRequest.getMediaTypeRequested())).build();
        }

        log.info("Error when updating priority:"+ serviceRequest.getParameter("priority") +" to value:"+serviceRequest.getBody()+" in channel:"+channelEndPoint);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(500).build();
    }

    public ServiceResponse removeSpecificPriority(ServiceRequest serviceRequest) {
        log.info("Attempting to delete priority:"+ serviceRequest.getParameter("priority") +" in channel:"+channelEndPoint);
        backingStore.removeChannelPriorityFieldValue(serviceRequest.getParameter("priority"));
        log.info("Successfully deleted priority:"+ serviceRequest.getParameter("priority") +" in channel:"+channelEndPoint);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).build();
    }

    public ServiceResponse getPrioritizedChannelContentsAsType(ServiceRequest serviceRequest) {
        final String contentsString = backingStore.serializePrioritizedAvailableContentsAsSummariesToType(
                serviceRequest.getParameter("priority"), serviceRequest.getMediaTypeRequested());
        return new ServiceResponse.ServiceResponseBuilder().setBody(contentsString).setReturnCode(200).build();
    }

    public ServiceResponse getBatchedChannelContentsAsType(ServiceRequest serviceRequest) {
        final String contentsString = backingStore.serializeBatchedAvailableContentsAsSummariesToType(
                serviceRequest.getParameter("batchId"), serviceRequest.getMediaTypeRequested());
        return new ServiceResponse.ServiceResponseBuilder().setBody(contentsString).setReturnCode(200).build();
    }

    public ServiceResponse getUnreservedChannelContentsAsType(ServiceRequest serviceRequest) {
        final String contentsString = backingStore.serializeUnreservedContentsAsSummariesToType(serviceRequest.getMediaTypeRequested());
        return new ServiceResponse.ServiceResponseBuilder().setBody(contentsString).setReturnCode(200).build();
    }

    public ServiceResponse takeSnapshot(ServiceRequest serviceRequest){
        log.info("Attempting to take snapshot of channel:"+channelEndPoint);
        final String fileDateId;
        try {
            fileDateId = backingStore.takeSnapshot();
            messageListenerNotification.takeSnapshot(fileDateId);
        } catch (Exception e) {
            log.warn("Exception taking snapshot of channel:"+channelEndPoint+" - "+e.getMessage());
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(500).
                        setBody(ExceptionRenderer.renderExceptionAsType(e, serviceRequest.getMediaTypeRequested())).build();
        }
        final String linkUri = new StringBuilder().append("http://").append(localIpAddress()).append(":").
                append(AbstractServer.PORT).append(channelEndPoint).append("/snapshots/").append(fileDateId).toString();

            final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                    addHeader(CustomHeaders.LOCATION, linkUri).build();
        final String message = "Successfully taken snapshot of channel:" + channelEndPoint;
        log.info(message);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).setServiceHeaders(serviceHeaders).
                setBody(new Serializer().toType(message,serviceRequest.getMediaTypeRequested())).build();
    }

    public ServiceResponse getSnapshotContentsAsType(final ServiceRequest serviceRequest) {
        final String contentsString = backingStore.serializeSnapshotContentsAsSummariesToType(
                serviceRequest.getParameter("snapshotId"),serviceRequest.getMediaTypeRequested());
        return new ServiceResponse.ServiceResponseBuilder().setBody(contentsString).setReturnCode(200).build();
    }

    public ServiceResponse getSnapshotListAsType(final ServiceRequest serviceRequest) {
        final List<String> snapshotsIdsList = backingStore.serializeSnapshotListToType();

        final List<String> snapshotsLinkTargetsList = new ArrayList<String>();
        for (String snapshotId : snapshotsIdsList) {
            snapshotsLinkTargetsList.add(new StringBuilder().append("http://").append(localIpAddress()).append(":").
                    append(AbstractServer.PORT).append(channelEndPoint).append("/snapshots/").append(snapshotId).toString());
        }
        final List<String> snapshotsLinkNamesList = new ArrayList<String>();
        for (String snapshotId : snapshotsIdsList) {
            try {
                snapshotsLinkNamesList.add(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(snapshotId)));
            } catch (ParseException e) {
                snapshotsLinkNamesList.add("");
            }
        }

        final List<Map<String, Object>> linkMapList = new ArrayList<Map<String, Object>>();

        for(int i=0;i<snapshotsIdsList.size();i++){
            final Map<String, Object> linkMap = new HashMap<String, Object>();
            linkMap.put("name",snapshotsLinkNamesList.get(i));
            linkMap.put("target",snapshotsLinkTargetsList.get(i));

            linkMap.put("restoreLink", new StringBuilder().append("http://").append(localIpAddress()).append(":").
                    append(AbstractServer.PORT).append(channelEndPoint).append("/restore/").append(snapshotsIdsList.get(i)).toString());
            
            linkMapList.add(linkMap);
        }

        return new ServiceResponse.ServiceResponseBuilder().setBody(new Serializer().toType(linkMapList, serviceRequest.getMediaTypeRequested(), "snapshotList")).
                setReturnCode(200).build();
    }

    public ServiceResponse restoreFromSnapshot(ServiceRequest serviceRequest){
        log.info("Attempting to restore from snapshot:"+serviceRequest.getParameter("snapshotId")+" of channel:"+channelEndPoint);
        String message;
        try {
            messageListenerNotification.restoreFromSnapshot(serviceRequest.getParameter("snapshotId"));
            message = backingStore.restoreFromSnapshot(serviceRequest.getParameter("snapshotId"));
        } catch (Exception e) {
            log.warn("Exception restoring from snapshot:"+serviceRequest.getParameter("snapshotId")+" of channel:"+channelEndPoint+" - "+e.getMessage());
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(500).
                        setBody(ExceptionRenderer.renderExceptionAsType(e, serviceRequest.getMediaTypeRequested())).build();
        }
        final String linkUri = new StringBuilder().append("http://").append(localIpAddress()).append(":").
                append(AbstractServer.PORT).append(channelEndPoint).toString();

            final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                    addHeader(CustomHeaders.LOCATION, linkUri).build();

        log.info("Successfully restored from snapshot:"+serviceRequest.getParameter("snapshotId")+" of channel:"+channelEndPoint);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).setServiceHeaders(serviceHeaders).
                setBody(new Serializer().toType(message,serviceRequest.getMediaTypeRequested())).build();
    }

    public ServiceResponse purgeChannel(ServiceRequest serviceRequest){
        log.info("Attempting to purge channel:"+channelEndPoint);
        String message;
        try {
            message = backingStore.purge();
        } catch (Exception e) {
            log.warn("Exception purging channel:"+channelEndPoint+" - "+e.getMessage());
                return new ServiceResponse.ServiceResponseBuilder().setReturnCode(500).
                        setBody(ExceptionRenderer.renderExceptionAsType(e, serviceRequest.getMediaTypeRequested())).build();
        }
        final String linkUri = new StringBuilder().append("http://").append(localIpAddress()).append(":").
                append(AbstractServer.PORT).append(channelEndPoint).append("/entries").toString();

        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                addHeader(CustomHeaders.LOCATION, linkUri).build();

        log.info("Successfully purged channel:"+channelEndPoint);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).setServiceHeaders(serviceHeaders).
                setBody(new Serializer().toType(message,serviceRequest.getMediaTypeRequested())).build();

    }

    public ServiceResponse registerMessageListener(final ServiceRequest serviceRequest){
        final MessageListenerAddress messageListenerAddress = new MessageListenerAddress();
        final String listenerId = serviceRequest.getParameter("messageListenerId");
        messageListenerAddress.setListenerId(listenerId);
        final String returnAddressesHeader = serviceRequest.getServiceHeaders().getSingleStringHeaderValueFromHeaders(CustomHeaders.RETURN_ADDRESSES);

        if(returnAddressesHeader==null){
            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(400).
                setBody(new Serializer().toType("Request is missing a return address header:"+CustomHeaders.RETURN_ADDRESSES.getName(), serviceRequest.getMediaTypeRequested())).build();
        }

        try {
            messageListenerAddress.setReturnAddress(ReturnAddress.parse(returnAddressesHeader));
        }
        catch (IllegalArgumentException e) {
            return new ServiceResponse.ServiceResponseBuilder().setReturnCode(400).
                setBody(new Serializer().toType(e.getMessage(), serviceRequest.getMediaTypeRequested())).build();
        }

        final String fullRegistrationUrl = serviceRequest.getParameter("fullRegistrationUrl");
        final String registrationUrl = serviceRequest.getParameter("registrationUrl");

        final RegistrationPoint registrationPoint = RegistrationPoint.valueOf(serviceRequest.getParameter("registrationPoint"));

        final Object[] filterArguments = serviceRequest.getParameter("filterArguments")==null?null:serviceRequest.getParameter("filterArguments").split(",");

        messageListenerNotification.registerMessageListener(messageListenerAddress, registrationUrl, registrationPoint, filterArguments);

        final String linkUri = new StringBuilder().append("http://").append(localIpAddress()).append(":").
                append(AbstractServer.PORT).append(channelEndPoint).append(fullRegistrationUrl).toString();

        final ServiceHeaders serviceHeaders = new ServiceHeaders.ServiceHeadersBuilder().
                addHeader(CustomHeaders.LOCATION, linkUri).build();

        final String message = "Successfully registered Message Listener:" + listenerId;
        log.info(message);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).setServiceHeaders(serviceHeaders).
                setBody(new Serializer().toType(message, serviceRequest.getMediaTypeRequested())).build();
    }

    public ServiceResponse unRegisterMessageListener(final ServiceRequest serviceRequest){
        final MessageListenerAddress messageListenerAddress = new MessageListenerAddress();
        final String listenerId = serviceRequest.getParameter("messageListenerId");
        messageListenerAddress.setListenerId(listenerId);

        final String url = serviceRequest.getParameter("registrationUrl");

        messageListenerNotification.unRegisterMessageListener(messageListenerAddress, url);

        final String message = "Successfully unregistered message listener:" + listenerId;
        log.info(message);
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).
                setBody(new Serializer().toType(message, serviceRequest.getMediaTypeRequested())).build();
    }

    public ServiceResponse getMessageListeners(final ServiceRequest serviceRequest) {
        final String registrationUrl = serviceRequest.getParameter("registrationUrl");
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).
                setBody(new Serializer().toType(messageListenerNotification.getMessageListeners(registrationUrl), serviceRequest.getMediaTypeRequested())).build();
    }

    public ServiceResponse getShutdownConfirmPage(final ServiceRequest serviceRequest){
        return new ServiceResponse.ServiceResponseBuilder().setReturnCode(200).
                setBody(new Serializer().toType(null, serviceRequest.getMediaTypeRequested(), "shutdownConfirmation")).build();
    }
}
