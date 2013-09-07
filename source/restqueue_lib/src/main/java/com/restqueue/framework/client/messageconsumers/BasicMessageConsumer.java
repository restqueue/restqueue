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
import com.restqueue.framework.service.entrywrappers.EntrySummary;
import com.restqueue.framework.service.entrywrappers.EntryWrapper;
import com.restqueue.framework.service.server.AbstractServer;
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
    private Integer serverPort = AbstractServer.PORT;
    private String urlLocation;
    private String messageConsumerId;
    private String eTag;
    private String batchId;
    private String priority;
    private String responseETag;
    private int responseCode;
    private static final HttpParams params = new BasicHttpParams();
    static{
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    }

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
        if(StringUtils.isNullOrEmpty(retrievalResult.getBody())){
            return new ArrayList<EntrySummary>();
        }
        return (List<EntrySummary>) new Serializer().fromType(retrievalResult.getBody(), MediaType.APPLICATION_JSON);
    }

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

        final EntryWrapper.EntryWrapperBuilder entryWrapperBuilder = new EntryWrapper.EntryWrapperBuilder().setContent(messageBody);

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_SEQUENCE)){
            entryWrapperBuilder.setSequence(Long.valueOf(retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_SEQUENCE).get(0)));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_DELAY)){
            entryWrapperBuilder.setDelay(retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_DELAY).get(0));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.CREATED_DATE)){
            try {
                entryWrapperBuilder.setCreated(DateUtils.unreadableDate(retrievalResult.getHeadersMap().get(CustomHeaders.CREATED_DATE).get(0)));
            } catch (ParseException e) {
                entryWrapperBuilder.setCreated(0);
            }
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.LAST_MODIFIED)){
            try {
                entryWrapperBuilder.setLastUpdated(DateUtils.unreadableDate(retrievalResult.getHeadersMap().get(CustomHeaders.LAST_MODIFIED).get(0)));
            } catch (ParseException e) {
                entryWrapperBuilder.setLastUpdated(0);
            }
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.CREATOR)){
            entryWrapperBuilder.setCreator(retrievalResult.getHeadersMap().get(CustomHeaders.CREATOR).get(0));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_CONSUMER_ID)){
            entryWrapperBuilder.setMessageConsumerId(retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_CONSUMER_ID).get(0));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.LOCATION)){
            final String locationValue = retrievalResult.getHeadersMap().get(CustomHeaders.LOCATION).get(0);
            entryWrapperBuilder.setLinkUri(locationValue);
            entryWrapperBuilder.setEntryId(locationValue.substring(locationValue.lastIndexOf("/") + 1));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.RETURN_ADDRESSES)){
            final List<String> valueList = retrievalResult.getHeadersMap().get(CustomHeaders.RETURN_ADDRESSES);
            entryWrapperBuilder.addReturnAddress(ReturnAddress.parse(valueList));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_BATCH_KEY)){
            final String batchKey = retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_BATCH_KEY).get(0);
            entryWrapperBuilder.setBatchKey(BatchKey.parse(batchKey));
        }

        if(retrievalResult.getHeadersMap().containsKey(CustomHeaders.MESSAGE_PRIORITY)){
            final String messagePriority = retrievalResult.getHeadersMap().get(CustomHeaders.MESSAGE_PRIORITY).get(0);
            entryWrapperBuilder.setPriority(Integer.valueOf(messagePriority));
        }

        return entryWrapperBuilder.build();
    }

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

    public void setChannelEndpoint(String channelEndpoint) {
        this.channelEndpoint = channelEndpoint;
    }

    public void setServerIpAddress(String serverIpAddress) {
        if(serverIpAddress!=null){
            this.serverIpAddress = serverIpAddress;
        }
    }

    public void setServerPort(Integer serverPort) {
        if(serverPort!=null){
            this.serverPort = serverPort;
        }
    }

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
}
