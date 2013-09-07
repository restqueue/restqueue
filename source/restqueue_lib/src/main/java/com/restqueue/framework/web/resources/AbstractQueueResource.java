package com.restqueue.framework.web.resources;

import com.restqueue.framework.service.backingstore.ChannelBackingStoreRepository;
import com.restqueue.framework.service.backingstoreduplicatesfilters.BackingStoreDuplicatesFilter;
import com.restqueue.framework.service.backingstorefilters.BackingStoreFilter;
import com.restqueue.framework.service.notification.MessageListenerNotificationRepository;
import com.restqueue.framework.service.notification.RegistrationPoint;
import com.restqueue.framework.service.resourcedelegate.ChannelResourceDelegate;
import com.restqueue.framework.service.transport.ServiceHeaders;
import com.restqueue.framework.service.transport.ServiceRequest;
import com.restqueue.framework.web.servicetransform.ServiceHeadersTransform;
import com.restqueue.framework.web.servicetransform.ServiceResponseTransform;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URISyntaxException;

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
 * Date: Jan 29, 2012
 * Time: 7:07:48 PM
 */
public abstract class AbstractQueueResource {

    private ChannelResourceDelegate channelResourceDelegate = new ChannelResourceDelegate(
            ChannelBackingStoreRepository.getOrCreateInstance(getConcreteResourceClass(),
            getApplicableFilterChain(), getDuplicatesFilter()), getImplementedResourceUrl(),
            MessageListenerNotificationRepository.getOrCreateNotificationInstance(getConcreteResourceClass()));

    protected abstract BackingStoreDuplicatesFilter getDuplicatesFilter();

    protected abstract BackingStoreFilter getApplicableFilterChain();

    protected abstract String getImplementedResourceUrl();

    protected final Class getConcreteResourceClass() {
        return this.getClass();
    }
    //GET methods for channel summary

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelSummaryAsJson() {
        return getChannelSummaryAsType(MediaType.APPLICATION_JSON);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getChannelSummaryAsXml() {
        return getChannelSummaryAsType(MediaType.APPLICATION_XML);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getChannelSummaryAsXhtml() {
        return getChannelSummaryAsType(MediaType.TEXT_HTML);
    }

    private Response getChannelSummaryAsType(String asType) {
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getChannelSummaryAsType(serviceRequest));
    }


    //GET methods for whole channel contents, public and private

    @GET
    @Path("/entries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelContentsAsJson(@Context HttpHeaders headers) {
        return getChannelContentsAsType(MediaType.APPLICATION_JSON, headers);
    }

    @GET
    @Path("/entries")
    @Produces(MediaType.APPLICATION_XML)
    public Response getChannelContentsAsXml(@Context HttpHeaders headers) {
        return getChannelContentsAsType(MediaType.APPLICATION_XML, headers);
    }

    @GET
    @Path("/entries")
    @Produces(MediaType.TEXT_HTML)
    public Response getChannelContentsAsXhtml(@Context HttpHeaders headers) {
        return getChannelContentsAsType(MediaType.TEXT_HTML, headers);
    }

    private Response getChannelContentsAsType(String asType, HttpHeaders headers) {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getChannelContentsAsType(serviceRequest));
    }

    //POST methods to post new messages onto the channel

    @POST
    @Path("/entries")
    @Consumes(MediaType.APPLICATION_XML)
    public Response addMessageToChannelAsXml(final String inXml, @Context HttpHeaders headers) throws URISyntaxException {
        return addMessageToChannelAsType(inXml, MediaType.APPLICATION_XML, headers);
    }

    @POST
    @Path("/entries")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMessageToChannelAsJson(final String inJson, @Context HttpHeaders headers) throws URISyntaxException {
        return addMessageToChannelAsType(inJson, MediaType.APPLICATION_JSON, headers);
    }

    private Response addMessageToChannelAsType(final String requestBody, final String asType, HttpHeaders headers) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setBody(requestBody).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.addMessageToChannelAsType(serviceRequest));
    }

    //GET methods for specific entry, public and private

    @GET
    @Path("/entries/{entryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecificEntryAsJson(@PathParam("entryId") final String entryId) {
        return getSpecificEntryAsType(entryId, MediaType.APPLICATION_JSON);
    }

    @GET
    @Path("/entries/{entryId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getSpecificEntryAsXml(@PathParam("entryId") final String entryId) {
        return getSpecificEntryAsType(entryId, MediaType.APPLICATION_XML);
    }

    private Response getSpecificEntryAsType(final String entryId, String asType) {
        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).
                addParameter("entryId",entryId).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getSpecificEntryAsType(serviceRequest));
    }

    //DELETE method to remove a specific entry
    @DELETE
    @Path("/entries/{entryId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response removeSpecificEntryAsXml(@PathParam("entryId") final String entryId) {
        return removeSpecificEntryAsType(entryId, MediaType.APPLICATION_XML);
    }

    @DELETE
    @Path("/entries/{entryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSpecificEntryAsJson(@PathParam("entryId") final String entryId) {
        return removeSpecificEntryAsType(entryId, MediaType.APPLICATION_JSON);
    }

    private Response removeSpecificEntryAsType(final String entryId, String asType) {
        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).
                addParameter("entryId",entryId).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.removeSpecificEntry(serviceRequest));
    }
    //PUT methods to update a specific entry

    @PUT
    @Path("/entries/{entryId}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateSpecificEntryAsXml(@PathParam("entryId") final String entryId, @Context HttpHeaders headers, final String inXml) {
        return updateSpecificEntryAsType(entryId, headers, MediaType.APPLICATION_XML, inXml);
    }
    @PUT
    @Path("/entries/{entryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateSpecificEntryAsJson(@PathParam("entryId") final String entryId, @Context HttpHeaders headers, final String inJson) {
        return updateSpecificEntryAsType(entryId, headers, MediaType.APPLICATION_JSON, inJson);
    }

    private Response updateSpecificEntryAsType(final String entryId, HttpHeaders headers, String asType, final String bodyContent) {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setBody(bodyContent).
                setServiceHeaders(serviceHeaders).addParameter("entryId",entryId).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.updateSpecificEntryAsType(serviceRequest));
    }

    //GET methods to retrieve meta information about the channel

    @GET
    @Path("/state")
    @Produces(MediaType.TEXT_HTML)
    public Response getChannelStateAsXhtml() {
        return getChannelStateAsType(MediaType.TEXT_HTML);
    }

    @GET
    @Path("/state")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelStateAsJson() {
        return getChannelStateAsType(MediaType.APPLICATION_JSON);
    }

    @GET
    @Path("/state")
    @Produces(MediaType.APPLICATION_XML)
    public Response getChannelStateAsXml() {
        return getChannelStateAsType(MediaType.APPLICATION_XML);
    }

    private Response getChannelStateAsType(final String asType){
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getChannelStateAsType(serviceRequest));
    }

    @GET
    @Path("/state/{stateField}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelStateFieldAsJson(@PathParam("stateField") String stateField) {
        return getChannelStateFieldAsType(stateField, MediaType.APPLICATION_JSON);
    }

    @GET
    @Path("/state/{stateField}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getChannelStateFieldAsXml(@PathParam("stateField") String stateField) {
        return getChannelStateFieldAsType(stateField, MediaType.APPLICATION_XML);
    }

    @GET
    @Path("/state/{stateField}")
    @Produces(MediaType.TEXT_HTML)
    public Response getChannelStateFieldAsXhtml(@PathParam("stateField") String stateField) {
        return getChannelStateFieldAsType(stateField, MediaType.TEXT_HTML);
    }

    private Response getChannelStateFieldAsType(String stateField, String asType) {
        final ServiceRequest serviceRequest = new ServiceRequest.ServiceRequestBuilder().
                addParameter("stateField",stateField).setMediaTypeRequested(asType).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getChannelStateField(serviceRequest));
    }

    //PUT methods to update a specific state field

    @PUT
    @Path("/state/{stateField}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSpecificStateFieldAsJson(@PathParam("stateField") final String stateField, @Context HttpHeaders headers, final String newValue) {
        return updateSpecificStateFieldAsType(stateField, headers, newValue, MediaType.APPLICATION_JSON);
    }

    @PUT
    @Path("/state/{stateField}")
    @Produces(MediaType.APPLICATION_XML)
    public Response updateSpecificStateFieldAsXml(@PathParam("stateField") final String stateField, @Context HttpHeaders headers, final String newValue) {
        return updateSpecificStateFieldAsType(stateField, headers, newValue, MediaType.APPLICATION_XML);
    }

    private Response updateSpecificStateFieldAsType(final String stateField, HttpHeaders headers, final String newValue, String asType) {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setBody(newValue).
                setServiceHeaders(serviceHeaders).addParameter("stateField",stateField).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.updateSpecificStateField(serviceRequest));
    }

    //GET methods for priorities

    @GET
    @Path("/state/prioritySettings/{priority}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getSpecificPrioritySettingAsXml(@PathParam("priority") final String priority, @Context HttpHeaders headers) {
        return getSpecificPrioritySettingAsType(priority, headers, MediaType.APPLICATION_XML);
    }

    @GET
    @Path("/state/prioritySettings/{priority}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecificPrioritySettingAsJson(@PathParam("priority") final String priority, @Context HttpHeaders headers) {
        return getSpecificPrioritySettingAsType(priority, headers, MediaType.APPLICATION_JSON);
    }

    public Response getSpecificPrioritySettingAsType(final String priority, HttpHeaders headers, String asType) {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).
                setServiceHeaders(serviceHeaders).addParameter("priority",priority).build();

        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getPriorityField(serviceRequest));
    }

    //PUT methods to update a specific priority

    @PUT
    @Path("/state/prioritySettings/{priority}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addOrUpdateSpecificPriorityAsJson(@PathParam("priority") final String priority, @Context HttpHeaders headers, final String newValue) {
        return addOrUpdateSpecificPriorityAsType(priority, headers, newValue, MediaType.APPLICATION_JSON);
    }

    @PUT
    @Path("/state/prioritySettings/{priority}")
    @Produces(MediaType.APPLICATION_XML)
    public Response addOrUpdateSpecificPriorityAsXml(@PathParam("priority") final String priority, @Context HttpHeaders headers, final String newValue) {
        return addOrUpdateSpecificPriorityAsType(priority, headers, newValue, MediaType.APPLICATION_XML);
    }

    private Response addOrUpdateSpecificPriorityAsType(final String stateField, HttpHeaders headers, final String newValue, String asType) {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setBody(newValue).
                setServiceHeaders(serviceHeaders).addParameter("priority",stateField).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.addOrUpdateSpecificPriority(serviceRequest));
    }

    //DELETE method to remove a specific priority

    @DELETE
    @Path("/state/prioritySettings/{priority}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSpecificPriorityAsJson(@PathParam("priority") final String priority, @Context HttpHeaders headers) {
        return removeSpecificPriorityAsType(priority, headers, MediaType.APPLICATION_JSON);
    }

    @DELETE
    @Path("/state/prioritySettings/{priority}")
    @Produces(MediaType.APPLICATION_XML)
    public Response removeSpecificPriorityAsXml(@PathParam("priority") final String priority, @Context HttpHeaders headers) {
        return removeSpecificPriorityAsType(priority, headers, MediaType.APPLICATION_XML);
    }

    private Response removeSpecificPriorityAsType(final String priority, HttpHeaders headers, String asType) {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).
                setServiceHeaders(serviceHeaders).addParameter("priority",priority).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.removeSpecificPriority(serviceRequest));
    }

    @GET
    @Path("/entries/priority/{priority}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrioritizedChannelContentsAsJson(@PathParam("priority") final String priority) {
        return getPrioritizedChannelContentsAsType(priority, MediaType.APPLICATION_JSON);
    }

    @GET
    @Path("/entries/priority/{priority}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getPrioritizedChannelContentsAsXml(@PathParam("priority") final String priority) {
        return getPrioritizedChannelContentsAsType(priority, MediaType.APPLICATION_XML);
    }

    @GET
    @Path("/entries/priority/{priority}")
    @Produces(MediaType.TEXT_HTML)
    public Response getPrioritizedChannelContentsAsXhtml(@PathParam("priority") final String priority) {
        return getPrioritizedChannelContentsAsType(priority, MediaType.TEXT_HTML);
    }

    private Response getPrioritizedChannelContentsAsType(String priority, String asType) {
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).
                addParameter("priority",priority).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getPrioritizedChannelContentsAsType(serviceRequest));
    }

    //GET methods for specific batchId

    @GET
    @Path("/entries/batch/{batchId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBatchedChannelContentsAsJson(@PathParam("batchId") final String batchId) {
        return getBatchedChannelContentsAsType(batchId, MediaType.APPLICATION_JSON);
    }

    @GET
    @Path("/entries/batch/{batchId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getBatchedChannelContentsAsXml(@PathParam("batchId") final String batchId) {
        return getBatchedChannelContentsAsType(batchId, MediaType.APPLICATION_XML);
    }

    @GET
    @Path("/entries/batch/{batchId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getBatchedChannelContentsAsXhtml(@PathParam("batchId") final String batchId) {
        return getBatchedChannelContentsAsType(batchId, MediaType.TEXT_HTML);
    }

    private Response getBatchedChannelContentsAsType(String batchId, String asType) {
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).
                addParameter("batchId",batchId).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getBatchedChannelContentsAsType(serviceRequest));
    }

    //POST methods to create new snapshot

    @POST
    @Path("/snapshots")
    @Produces(MediaType.APPLICATION_XML)
    public Response takeSnapshotAsXml(@Context HttpHeaders headers) throws URISyntaxException {
        return takeSnapshotAsType(MediaType.APPLICATION_XML, headers);
    }

    @POST
    @Path("/snapshots")
    @Produces(MediaType.TEXT_HTML)
    public Response takeSnapshotAsXhtml(@Context HttpHeaders headers) throws URISyntaxException {
        return takeSnapshotAsType(MediaType.TEXT_HTML, headers);
    }

    @POST
    @Path("/snapshots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response takeSnapshotAsJson(@Context HttpHeaders headers) throws URISyntaxException {
        return takeSnapshotAsType(MediaType.APPLICATION_JSON, headers);
    }

    private Response takeSnapshotAsType(final String asType, HttpHeaders headers) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.takeSnapshot(serviceRequest));
    }

    //GET methods for specific snapshot

    @GET
    @Path("/snapshots/{snapshotId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSnapshotAsJson(@PathParam("snapshotId") final String snapshotId) {
        return getSnapshotAsType(snapshotId, MediaType.APPLICATION_JSON);
    }

    @GET
    @Path("/snapshots/{snapshotId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getSnapshotAsXml(@PathParam("snapshotId") final String snapshotId) {
        return getSnapshotAsType(snapshotId, MediaType.APPLICATION_XML);
    }

    @GET
    @Path("/snapshots/{snapshotId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getSnapshotAsXhtml(@PathParam("snapshotId") final String snapshotId) {
        return getSnapshotAsType(snapshotId, MediaType.TEXT_HTML);
    }

    private Response getSnapshotAsType(String snapshotId, String asType) {
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).
                addParameter("snapshotId",snapshotId).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getSnapshotContentsAsType(serviceRequest));
    }

    //GET methods for snapshot list

    @GET
    @Path("/snapshots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSnapshotListAsJson() {
        return getSnapshotListAsType(MediaType.APPLICATION_JSON);
    }

    @GET
    @Path("/snapshots")
    @Produces(MediaType.APPLICATION_XML)
    public Response getSnapshotListAsXml() {
        return getSnapshotListAsType(MediaType.APPLICATION_XML);
    }

    @GET
    @Path("/snapshots")
    @Produces(MediaType.TEXT_HTML)
    public Response getSnapshotListAsXhtml() {
        return getSnapshotListAsType(MediaType.TEXT_HTML);
    }

    private Response getSnapshotListAsType(String asType) {
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getSnapshotListAsType(serviceRequest));
    }

    //POST methods to restore from snapshot

    @POST
    @Path("/restore/{snapshotId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response restoreFromSnapshotAsXml(@PathParam("snapshotId") final String snapshotId, @Context HttpHeaders headers) throws URISyntaxException {
        return restoreFromSnapshotAsType(snapshotId, MediaType.APPLICATION_XML, headers);
    }

    @POST
    @Path("/restore/{snapshotId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response restoreFromSnapshotAsJson(@PathParam("snapshotId") final String snapshotId, @Context HttpHeaders headers) throws URISyntaxException {
        return restoreFromSnapshotAsType(snapshotId, MediaType.APPLICATION_JSON, headers);
    }

    @POST
    @Path("/restore/{snapshotId}")
    @Produces(MediaType.TEXT_HTML)
    public Response restoreFromSnapshotAsXhtml(@PathParam("snapshotId") final String snapshotId, @Context HttpHeaders headers) throws URISyntaxException {
        return restoreFromSnapshotAsType(snapshotId, MediaType.TEXT_HTML, headers);
    }

    private Response restoreFromSnapshotAsType(final String snapshotId, final String asType, HttpHeaders headers) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).
        addParameter("snapshotId",snapshotId).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.restoreFromSnapshot(serviceRequest));
    }

    //POST methods to purge

    @POST
    @Path("/purge")
    @Produces(MediaType.APPLICATION_XML)
    public Response purgeChannelAsXml(@Context HttpHeaders headers) throws URISyntaxException {
        return purgeChannelAsType(MediaType.APPLICATION_XML, headers);
    }

    @POST
    @Path("/purge")
    @Produces(MediaType.APPLICATION_JSON)
    public Response purgeChannelAsJson(@Context HttpHeaders headers) throws URISyntaxException {
        return purgeChannelAsType(MediaType.APPLICATION_JSON, headers);
    }

    @POST
    @Path("/purge")
    @Produces(MediaType.TEXT_HTML)
    public Response purgeChannelAsXhtml(@Context HttpHeaders headers) throws URISyntaxException {
        return purgeChannelAsType(MediaType.TEXT_HTML, headers);
    }

    private Response purgeChannelAsType(final String asType, HttpHeaders headers) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest serviceRequest=new ServiceRequest.ServiceRequestBuilder().setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.purgeChannel(serviceRequest));
    }

    //GET methods to lookup message listeners
    @GET
    @Path("/registration/messagelisteners")
    @Produces(MediaType.APPLICATION_XML)
    public Response getMessageListenersAsXml(@Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersAsType(MediaType.APPLICATION_XML, headers);
    }

    @GET
    @Path("/registration/messagelisteners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageListenersAsJson(@Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersAsType(MediaType.APPLICATION_JSON, headers);
    }

    @GET
    @Path("/registration/messagelisteners")
    @Produces(MediaType.TEXT_HTML)
    public Response getMessageListenersAsXhtml(@Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersAsType(MediaType.TEXT_HTML, headers);
    }

    private Response getMessageListenersAsType(final String asType, HttpHeaders headers) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("registrationUrl","/registration/messagelisteners");
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getMessageListeners(serviceRequest));
    }

    //PUT methods to register message listeners

    @PUT
    @Path("/registration/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response registerMessageListenerAsXml(@PathParam("listenerId") final String listenerId, @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerAsType(MediaType.APPLICATION_XML, headers, listenerId);
    }

    @PUT
    @Path("/registration/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerMessageListenerAsJson(@PathParam("listenerId") final String listenerId, @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerAsType(MediaType.APPLICATION_JSON, headers, listenerId);
    }

    @PUT
    @Path("/registration/messagelisteners/{listenerId}")
    @Produces(MediaType.TEXT_HTML)
    public Response registerMessageListenerAsXhtml(@PathParam("listenerId") final String listenerId, @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerAsType(MediaType.TEXT_HTML, headers, listenerId);
    }

    private Response registerMessageListenerAsType(final String asType, HttpHeaders headers, final String listenerId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("messageListenerId",listenerId);
        serviceRequestBuilder.addParameter("fullRegistrationUrl","/registration/messagelisteners/"+listenerId);
        serviceRequestBuilder.addParameter("registrationUrl","/registration/messagelisteners");
        serviceRequestBuilder.addParameter("registrationPoint", RegistrationPoint.ALL.name());
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.registerMessageListener(serviceRequest));
    }

    //DELETE methods to unRegister message listeners

    @DELETE
    @Path("/registration/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response unRegisterMessageListenerAsXml(@PathParam("listenerId") final String listenerId, @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerAsType(MediaType.APPLICATION_XML, headers, listenerId);
    }

    @DELETE
    @Path("/registration/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unRegisterMessageListenerAsJson(@PathParam("listenerId") final String listenerId, @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerAsType(MediaType.APPLICATION_JSON, headers, listenerId);
    }

    @DELETE
    @Path("/registration/messagelisteners/{listenerId}")
    @Produces(MediaType.TEXT_HTML)
    public Response unRegisterMessageListenerAsXhtml(@PathParam("listenerId") final String listenerId, @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerAsType(MediaType.TEXT_HTML, headers, listenerId);
    }

    private Response unRegisterMessageListenerAsType(final String asType, HttpHeaders headers, final String listenerId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("messageListenerId",listenerId);
        serviceRequestBuilder.addParameter("registrationUrl","/registration/messagelisteners");
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.unRegisterMessageListener(serviceRequest));
    }

    //GET methods to lookup message listeners
    @GET
    @Path("/registration/batch/{batchId}/messagelisteners")
    @Produces(MediaType.APPLICATION_XML)
    public Response getMessageListenersForBatchAsXml(@PathParam("batchId") final String batchId, @Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersForBatchAsType(MediaType.APPLICATION_XML, headers, batchId);
    }

    @GET
    @Path("/registration/batch/{batchId}/messagelisteners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageListenersForBatchAsJson(@PathParam("batchId") final String batchId, @Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersForBatchAsType(MediaType.APPLICATION_JSON, headers, batchId);
    }

    @GET
    @Path("/registration/batch/{batchId}/messagelisteners")
    @Produces(MediaType.TEXT_HTML)
    public Response getMessageListenersForBatchAsXhtml(@PathParam("batchId") final String batchId, @Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersForBatchAsType(MediaType.TEXT_HTML, headers, batchId);
    }

    private Response getMessageListenersForBatchAsType(final String asType, HttpHeaders headers, String batchId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("registrationUrl","/registration/batch/"+batchId+"/messagelisteners");
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getMessageListeners(serviceRequest));
    }


    //PUT methods to register message listeners

    @PUT
    @Path("/registration/batch/{batchId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response registerMessageListenerForBatchAsXml(@PathParam("listenerId") final String listenerId, @PathParam("batchId") final String batchId,
                                                         @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerForBatchAsType(MediaType.APPLICATION_XML, batchId, headers, listenerId);
    }

    @PUT
    @Path("/registration/batch/{batchId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerMessageListenerForBatchAsJson(@PathParam("listenerId") final String listenerId, @PathParam("batchId") final String batchId,
                                                          @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerForBatchAsType(MediaType.APPLICATION_JSON, batchId, headers, listenerId);
    }

    @PUT
    @Path("/registration/batch/{batchId}/messagelisteners/{listenerId}")
    @Produces(MediaType.TEXT_HTML)
    public Response registerMessageListenerForBatchAsXhtml(@PathParam("listenerId") final String listenerId, @PathParam("batchId") final String batchId,
                                                           @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerForBatchAsType(MediaType.TEXT_HTML, batchId, headers, listenerId);
    }

    private Response registerMessageListenerForBatchAsType(final String asType, final String batchId, HttpHeaders headers, final String listenerId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("messageListenerId",listenerId);
        serviceRequestBuilder.addParameter("fullRegistrationUrl","/registration/batch/"+batchId+"/messagelisteners/"+listenerId);
        serviceRequestBuilder.addParameter("registrationUrl","/registration/batch/"+batchId+"/messagelisteners");
        serviceRequestBuilder.addParameter("registrationPoint", RegistrationPoint.SPECIFIC_BATCH.name());
        serviceRequestBuilder.addParameter("filterArguments",batchId);
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.registerMessageListener(serviceRequest));
    }

    //DELETE methods to unRegister message listeners

    @DELETE
    @Path("/registration/batch/{batchId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response unRegisterMessageListenerForBatchAsXml(@PathParam("listenerId") final String listenerId, @PathParam("batchId") final String batchId, @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerForBatchAsType(MediaType.APPLICATION_XML, batchId, headers, listenerId);
    }

    @DELETE
    @Path("/registration/batch/{batchId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unRegisterMessageListenerForBatchAsJson(@PathParam("listenerId") final String listenerId, @PathParam("batchId") final String batchId, @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerForBatchAsType(MediaType.APPLICATION_JSON, batchId, headers, listenerId);
    }

    @DELETE
    @Path("/registration/batch/{batchId}/messagelisteners/{listenerId}")
    @Produces(MediaType.TEXT_HTML)
    public Response unRegisterMessageListenerForBatchAsXhtml(@PathParam("listenerId") final String listenerId, @PathParam("batchId") final String batchId, @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerForBatchAsType(MediaType.TEXT_HTML, batchId, headers, listenerId);
    }

    private Response unRegisterMessageListenerForBatchAsType(final String asType, final String batchId, HttpHeaders headers, final String listenerId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("messageListenerId",listenerId);
        serviceRequestBuilder.addParameter("registrationUrl","/registration/batch/"+batchId+"/messagelisteners");
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.unRegisterMessageListener(serviceRequest));
    }

   //GET methods to lookup message listeners
    @GET
    @Path("/registration/priority/{priorityId}/messagelisteners")
    @Produces(MediaType.APPLICATION_XML)
    public Response getMessageListenersForPriorityAsXml(@PathParam("priorityId") final String priorityId, @Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersForPriorityAsType(MediaType.APPLICATION_XML, headers, priorityId);
    }

    @GET
    @Path("/registration/priority/{priorityId}/messagelisteners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessageListenersForPriorityAsJson(@PathParam("priorityId") final String priorityId, @Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersForPriorityAsType(MediaType.APPLICATION_JSON, headers, priorityId);
    }

    @GET
    @Path("/registration/priority/{priorityId}/messagelisteners")
    @Produces(MediaType.TEXT_HTML)
    public Response getMessageListenersForPriorityAsXhtml(@PathParam("priorityId") final String priorityId, @Context HttpHeaders headers) throws URISyntaxException {
        return getMessageListenersForPriorityAsType(MediaType.TEXT_HTML, headers, priorityId);
    }

    private Response getMessageListenersForPriorityAsType(final String asType, HttpHeaders headers, String priorityId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("registrationUrl","/registration/priority/"+priorityId+"/messagelisteners");
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.getMessageListeners(serviceRequest));
    }

    //PUT methods to register message listeners

    @PUT
    @Path("/registration/priority/{priorityId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response registerMessageListenerForPriorityAsXml(@PathParam("listenerId") final String listenerId, @PathParam("priorityId") final String priorityId,
                                                         @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerForPriorityAsType(MediaType.APPLICATION_XML, priorityId, headers, listenerId);
    }

    @PUT
    @Path("/registration/priority/{priorityId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerMessageListenerForPriorityAsJson(@PathParam("listenerId") final String listenerId, @PathParam("priorityId") final String priorityId,
                                                          @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerForPriorityAsType(MediaType.APPLICATION_JSON, priorityId, headers, listenerId);
    }

    @PUT
    @Path("/registration/priority/{priorityId}/messagelisteners/{listenerId}")
    @Produces(MediaType.TEXT_HTML)
    public Response registerMessageListenerForPriorityAsXhtml(@PathParam("listenerId") final String listenerId, @PathParam("priorityId") final String priorityId,
                                                           @Context HttpHeaders headers) throws URISyntaxException {
        return registerMessageListenerForPriorityAsType(MediaType.TEXT_HTML, priorityId, headers, listenerId);
    }

    private Response registerMessageListenerForPriorityAsType(final String asType, final String priorityId, HttpHeaders headers, final String listenerId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("messageListenerId",listenerId);
        serviceRequestBuilder.addParameter("fullRegistrationUrl","/registration/priority/"+priorityId+"/messagelisteners/"+listenerId);
        serviceRequestBuilder.addParameter("registrationUrl","/registration/priority/"+priorityId+"/messagelisteners");
        serviceRequestBuilder.addParameter("registrationPoint",RegistrationPoint.SPECIFIC_PRIORITY.name());
        serviceRequestBuilder.addParameter("filterArguments",priorityId);
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.registerMessageListener(serviceRequest));
    }

    //DELETE methods to unRegister message listeners

    @DELETE
    @Path("/registration/priority/{priorityId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response unRegisterMessageListenerForPriorityAsXml(@PathParam("listenerId") final String listenerId, @PathParam("priorityId") final String priorityId,
                                                              @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerForPriorityAsType(MediaType.APPLICATION_XML, priorityId, headers, listenerId);
    }

    @DELETE
    @Path("/registration/priority/{priorityId}/messagelisteners/{listenerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unRegisterMessageListenerForPriorityAsJson(@PathParam("listenerId") final String listenerId, @PathParam("priorityId") final String priorityId,
                                                               @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerForPriorityAsType(MediaType.APPLICATION_JSON, priorityId, headers, listenerId);
    }

    @DELETE
    @Path("/registration/priority/{priorityId}/messagelisteners/{listenerId}")
    @Produces(MediaType.TEXT_HTML)
    public Response unRegisterMessageListenerForPriorityAsXhtml(@PathParam("listenerId") final String listenerId, @PathParam("priorityId") final String priorityId,
                                                                @Context HttpHeaders headers) throws URISyntaxException {
        return unRegisterMessageListenerForPriorityAsType(MediaType.TEXT_HTML, priorityId, headers, listenerId);
    }

    private Response unRegisterMessageListenerForPriorityAsType(final String asType, final String priorityId, HttpHeaders headers, final String listenerId) throws URISyntaxException {
        final ServiceHeaders serviceHeaders= ServiceHeadersTransform.serviceHeadersFromHttpHeaders(headers);
        final ServiceRequest.ServiceRequestBuilder serviceRequestBuilder = new ServiceRequest.ServiceRequestBuilder();
        serviceRequestBuilder.addParameter("messageListenerId",listenerId);
        serviceRequestBuilder.addParameter("registrationUrl","/registration/priority/"+priorityId+"/messagelisteners");
        final ServiceRequest serviceRequest= serviceRequestBuilder.setMediaTypeRequested(asType).setServiceHeaders(serviceHeaders).build();
        return ServiceResponseTransform.httpResponseFromServiceResponse(channelResourceDelegate.unRegisterMessageListener(serviceRequest));
    }
}
