package com.restqueue.framework.client.messageconsumers;

import com.restqueue.common.utils.DateUtils;
import com.restqueue.common.utils.StringUtils;
import com.restqueue.common.utils.URLUtils;
import com.restqueue.framework.client.common.entryfields.BatchKey;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.results.ExecutionResult;
import com.restqueue.framework.client.results.Result;
import com.restqueue.framework.client.results.ResultsFactory;
import com.restqueue.framework.client.results.RetrievalResult;
import com.restqueue.framework.client.entrywrappers.EntrySummary;
import com.restqueue.framework.client.entrywrappers.EntryWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates the functionality that a message consumer would need when dealing with messages on a channel.<BR/><BR/>
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
 * Date: Jan 26, 2011
 * Time: 8:19:15 PM
 */
public class BasicMessageConsumer {
    private String channelEndpoint;
    private String serverIpAddress = "localhost";
    private Integer serverPort = 9998;
    private String urlLocation;
    private String messageConsumerId;
    private String eTag;
    private String batchId;
    private String priority;
    private String responseETag;
    private int responseCode;
    private boolean contentsChanged =true;
    private static final HttpParams params = new BasicHttpParams();
    static{
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    }

    /**
     * This gets the channel contents. If the server is running with caching enabled this method deals with conditional GET
     * which means that if you set the eTag before calling this method then if the channel contents are unchanged since the
     * last time this method was called, it will return an empty list and the boolean field contentsChanged is set to false.
     *
     * If you want to just get the contents of a specific batch, use setBatchID() before calling this method.
     *
     * If you want to just get the contents of the channel that have a specific priority, use setPriority() before calling this method.
     *
     * You cannot filter by batchID and priority at the same time.
     */
    public List<EntrySummary> getAllMessages() {
        if(channelEndpoint==null){
            throw new ChannelClientException("Must set the channel endpoint to get the messages from.",
                    ChannelClientException.ExceptionType.MISSING_DATA);
        }

        String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) + "/entries";

        if(batchId!=null && priority!=null){
            throw new ChannelClientException("Cannot filter by batch ID and priority at the same time",
                    ChannelClientException.ExceptionType.BAD_STATE);
        }

        if(batchId!=null){
            fullChannelEndpoint=fullChannelEndpoint+"/batch/"+batchId;
        }

        if(priority!=null){
            fullChannelEndpoint=fullChannelEndpoint+"/priority/"+priority;
        }

        final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
        httpGet.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        if(StringUtils.isNotNullAndNotEmpty(eTag)){
            httpGet.addHeader(HttpHeaders.IF_NONE_MATCH, eTag);
        }
        DefaultHttpClient client = new DefaultHttpClient(params);
        final HttpResponse getResponse;
        try {
            getResponse = client.execute(httpGet);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server at ip address:"+serverIpAddress+", port:"+
                    serverPort+". Ensure server is running and configured using the right ip address and port.",e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server at ip address:"+serverIpAddress+", port:"+
                                serverPort+".",e,
                                ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to get the messages:", e, ChannelClientException.ExceptionType.UNKNOWN);
        }

        final RetrievalResult retrievalResult = new ResultsFactory().retrievalResultFromHttpGetResponse(getResponse);
        responseETag=retrievalResult.getEtag();
        responseCode=retrievalResult.getResponseCode();

        contentsChanged = responseCode != 304;

        if(StringUtils.isNullOrEmpty(retrievalResult.getBody())){
            return new ArrayList<EntrySummary>();
        }
        return (List<EntrySummary>) new Serializer().fromType(retrievalResult.getBody(), MediaType.APPLICATION_JSON);
    }

    /**
     * This method sets the Message Consumer ID on a specific message in the channel. This effectively reserves the message
     * for that particular Message Consumer.
     *
     * You must set the Message Consumer ID first using setMessageConsumerId(String messageConsumerId), the eTag value using
     * setETag(String eTag) and the Location of the message to reserve using setUrlLocation(String location) before calling this method.
     */
    public void reserveMessage() {
        if(urlLocation==null){
            throw new ChannelClientException("Must set the URL Location for the message to reserve.",
                    ChannelClientException.ExceptionType.MISSING_DATA);
        }
        if(eTag==null){
            throw new ChannelClientException("Must set the eTag value to reserve a message.",
                    ChannelClientException.ExceptionType.MISSING_DATA);
        }
        if(messageConsumerId==null){
            throw new ChannelClientException("Must set the message consumer ID to reserve a message.",
                    ChannelClientException.ExceptionType.MISSING_DATA);
        }

        final HttpPut httpPut = new HttpPut(urlLocation);
        httpPut.setHeader(HttpHeaders.IF_MATCH, eTag);
        httpPut.setHeader(CustomHeaders.MESSAGE_CONSUMER_ID.getName(), messageConsumerId);

        DefaultHttpClient conditionalPutClient = new DefaultHttpClient(params);

        final Result conditionalPutResult;
        try {
            final HttpResponse conditionalPutResponse = conditionalPutClient.execute(httpPut);
            conditionalPutResult = new ResultsFactory().conditionalPutResultFromHttpPutResponse(conditionalPutResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server at ip address:" + serverIpAddress + ", port:" +
                    serverPort + ". Ensure server is running and configured using the right ip address and port.",e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server at ip address:"+serverIpAddress+", port:"+
                                serverPort+".",e,
                                ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to reserve the message:", e, ChannelClientException.ExceptionType.UNKNOWN);
        }

        if(!conditionalPutResult.isSuccess()){
            throw new ChannelClientException("Could not reserve message", ChannelClientException.ExceptionType.UNKNOWN);
        }

    }

    /**
     * This method gets a specified message as an EntryWrapper.
     *
     * You must set the Location of the message to get using setUrlLocation(String location) before calling this method.
     *
     * @return The message as an EntryWrapper
     */
    public EntryWrapper getMessage(){
        if(urlLocation==null){
            throw new ChannelClientException("Must set the URL Location for the message to get.",
                    ChannelClientException.ExceptionType.MISSING_DATA);
        }

        //get specific message
        final HttpGet httpGet = new HttpGet(urlLocation);
        httpGet.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);        
        DefaultHttpClient client = new DefaultHttpClient(params);
        final RetrievalResult retrievalResult;
        try {
            final HttpResponse getResponse = client.execute(httpGet);
            retrievalResult = new ResultsFactory().retrievalResultFromHttpGetResponse(getResponse);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server at ip address:"+serverIpAddress+", port:"+
                                serverPort+".",e,
                                ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to get the message at "+urlLocation+" :",e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }

        final Object messageBody = new Serializer().fromType(retrievalResult.getBody(), MediaType.APPLICATION_JSON);


        final EntryWrapper entryWrapper = new EntryWrapper();
        entryWrapper.setContent(messageBody);

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_SEQUENCE)){
            entryWrapper.setSequence(Long.valueOf(retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_SEQUENCE).get(0)));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_DELAY)){
            entryWrapper.setDelay(retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_DELAY).get(0));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.CREATED_DATE)){
            try {
                entryWrapper.setCreated(DateUtils.unreadableDate(retrievalResult.getHeadersMap().get(CustomHeaders.CREATED_DATE).get(0)));
            } catch (ParseException e) {
                entryWrapper.setCreated(0);
            }
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.LAST_MODIFIED)){
            try {
                entryWrapper.setLastUpdated(DateUtils.unreadableDate(retrievalResult.getHeadersMap().get(CustomHeaders.LAST_MODIFIED).get(0)));
            } catch (ParseException e) {
                entryWrapper.setLastUpdated(0);
            }
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.CREATOR)){
            entryWrapper.setCreator(retrievalResult.getHeadersMap().get(CustomHeaders.CREATOR).get(0));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_CONSUMER_ID)){
            entryWrapper.setMessageConsumerId(retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_CONSUMER_ID).get(0));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.LOCATION)){
            final String locationValue = retrievalResult.getHeadersMap().get(CustomHeaders.LOCATION).get(0);
            entryWrapper.setLinkUri(locationValue);
            entryWrapper.setEntryId(locationValue.substring(locationValue.lastIndexOf("/") + 1));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.RETURN_ADDRESSES)){
            final List<String> valueList = retrievalResult.getHeadersMap().get(CustomHeaders.RETURN_ADDRESSES);
            for (ReturnAddress returnAddress : ReturnAddress.parse(valueList)) {
                entryWrapper.addReturnAddress(returnAddress);
            }
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_BATCH_KEY)){
            final String batchKey = retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_BATCH_KEY).get(0);
            entryWrapper.setBatchKey(BatchKey.parse(batchKey));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_PRIORITY)){
            final String messagePriority = retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_PRIORITY).get(0);
            entryWrapper.setPriority(Integer.valueOf(messagePriority));
        }

        return entryWrapper;
    }

    /**
     * This method deletes a specified message from the channel.
     *
     * You must set the Location of the message to delete using setUrlLocation(String location) before calling this method.
     */
    public void deleteMessage() {
        if(urlLocation==null){
            throw new ChannelClientException("Must set the URL Location for the message to delete.",
                    ChannelClientException.ExceptionType.MISSING_DATA);
        }

        final HttpDelete httpDelete = new HttpDelete(urlLocation);
        ExecutionResult executionResult;
        try {
            final HttpResponse deleteResponse = new DefaultHttpClient().execute(httpDelete);

            executionResult = new ResultsFactory().executionResultFromHttpDeleteResponse(deleteResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.",e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.",e,
                                ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to delete a message:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }

        if(!executionResult.isSuccess()){
            throw new ChannelClientException("Could not delete message", ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    /**
     * Set the channel to perform the operation on.
     * @param channelEndpoint The channel endpoint (eg. http://{serverip}:{serverport}/channels/1.0/{channelName})
     */
    public void setChannelEndpoint(String channelEndpoint) {
        this.channelEndpoint = channelEndpoint;
    }

    /**
     * Set the server ip address where the channel is hosted. The default is localhost.
     * @param serverIpAddress The IP Address
     */
    public void setServerIpAddress(String serverIpAddress) {
        if(serverIpAddress!=null){
            this.serverIpAddress = serverIpAddress;
        }
    }

    /**
     * Set the server port where the channel is hosted. The default is 9998.
     * @param serverPort The port
     */
    public void setServerPort(Integer serverPort) {
        if(serverPort!=null){
            this.serverPort = serverPort;
        }
    }

    /**
     * Set the full URL of the message to operate on.
     * (eg. http://{serverip}:{serverport}/channels/1.0/{channelName}/entries/{entryid})
     */
    public void setUrlLocation(String urlLocation) {
        this.urlLocation = urlLocation;
    }

    public void setMessageConsumerId(String messageConsumerId) {
        this.messageConsumerId = messageConsumerId;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getResponseETag() {
        return responseETag;
    }

    public int getResponseCode() {
        return responseCode;
    }

    /**
     * To determine whether the contents of the channel have been updated since the last time.
     * @return contents have changed true/false
     */

    public boolean haveContentsChanged() {
        return contentsChanged;
    }
}
