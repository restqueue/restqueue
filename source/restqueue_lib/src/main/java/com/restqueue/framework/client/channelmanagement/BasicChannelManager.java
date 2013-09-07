package com.restqueue.framework.client.channelmanagement;

import com.restqueue.common.utils.URLUtils;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.results.ExecutionResult;
import com.restqueue.framework.client.results.Result;
import com.restqueue.framework.client.results.ResultsFactory;
import com.restqueue.framework.client.results.RetrievalResult;
import com.restqueue.framework.service.channelstate.BatchStrategy;
import com.restqueue.framework.service.channelstate.SequenceStrategy;
import com.restqueue.framework.service.server.AbstractServer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;

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
 * Date: Jan 13, 2011
 * Time: 7:14:38 PM
 */
public class BasicChannelManager {
    private String channelEndpoint;
    private String serverIpAddress = "localhost";
    private Integer serverPort = AbstractServer.PORT;
    private static final HttpParams params = new BasicHttpParams();
    static{
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    }

    public long getChannelMaxSize() {
        final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) + "/state/maxSize";

        //get state max size
        final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
        DefaultHttpClient client = new DefaultHttpClient(params);
        final HttpResponse response;
        try {
            response = client.execute(httpGet);
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
            throw new ChannelClientException("Unknown exception occurred when trying to get channel max size:", e, ChannelClientException.ExceptionType.UNKNOWN);
        }

        final RetrievalResult retrievalResult = new ResultsFactory().retrievalResultFromHttpGetResponse(response);
        return Long.valueOf(retrievalResult.getBody());
    }

    public Result updateChannelMaxSize(long maxSize) {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) + "/state/maxSize";

            //get state max size
            final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
            DefaultHttpClient client = new DefaultHttpClient(params);
            final HttpResponse response = client.execute(httpGet);

            //conditionally put new max size
            final HttpPut httpPut = new HttpPut(fullChannelEndpoint);
            httpPut.setEntity(createStringEntity(String.valueOf(maxSize)));
            httpPut.setHeader(HttpHeaders.IF_MATCH, response.getFirstHeader(HttpHeaders.ETAG).getValue());

            DefaultHttpClient conditionalPutClient = new DefaultHttpClient(params);
            final HttpResponse conditionalPutResponse = conditionalPutClient.execute(httpPut);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(conditionalPutResponse);
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
            throw new ChannelClientException("Unknown exception occurred when trying to update the channel max size:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }

    }

    public Result updateNextMessageSequence(long nextMessageSequence) {
        try {
            final String fullChannelEndpoint =
                    URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) + "/state/nextMessageSequence";

            //get current value
            final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
            DefaultHttpClient client = new DefaultHttpClient(params);
            final HttpResponse response = client.execute(httpGet);

            //conditionally put new value
            final HttpPut httpPut = new HttpPut(fullChannelEndpoint);
            httpPut.setEntity(createStringEntity(String.valueOf(nextMessageSequence)));
            httpPut.setHeader(HttpHeaders.IF_MATCH, response.getFirstHeader(HttpHeaders.ETAG).getValue());

            DefaultHttpClient conditionalPutClient = new DefaultHttpClient(params);
            final HttpResponse conditionalPutResponse = conditionalPutClient.execute(httpPut);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(conditionalPutResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to update the channel message sequence:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result drain() {
        return updateChannelMaxSize(0);
    }

    public Result incrementNextMessageSequence(int currentSequence) {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/state/nextMessageSequence";

            //get state next Message Sequence
            final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
            DefaultHttpClient client = new DefaultHttpClient(params);
            final HttpResponse getResponse = client.execute(httpGet);
            final RetrievalResult retrievalResult = new ResultsFactory().retrievalResultFromHttpGetResponse(getResponse);

            if (!retrievalResult.getBody().equals(String.valueOf(currentSequence))) {
                throw new RuntimeException("Concurrency exception: cannot increment nextMessageSequence on " +
                        URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                        " - it is currently " + retrievalResult.getBody() + " not " + currentSequence);
            }

            //conditionally put new next Message Sequence
            final HttpPut httpPut = new HttpPut(fullChannelEndpoint);
            httpPut.setEntity(createStringEntity(String.valueOf(currentSequence + 1)));
            httpPut.setHeader(HttpHeaders.IF_MATCH, getResponse.getFirstHeader(HttpHeaders.ETAG).getValue());

            DefaultHttpClient conditionalPutClient = new DefaultHttpClient(params);
            final HttpResponse conditionalPutResponse = conditionalPutClient.execute(httpPut);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(conditionalPutResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to increment next message sequence:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result updateChannelPriority(String channelPriority, int priorityFromValue, String eTag) {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/state/prioritySettings/" + channelPriority;

            final HttpPut httpPut = new HttpPut(fullChannelEndpoint);
            httpPut.setEntity(createStringEntity(String.valueOf(priorityFromValue)));

            if (eTag != null) {
                httpPut.setHeader(HttpHeaders.IF_MATCH, eTag);
            }

            DefaultHttpClient conditionalPutClient = new DefaultHttpClient(params);
            final HttpResponse conditionalPutResponse = conditionalPutClient.execute(httpPut);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(conditionalPutResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to update channel priority:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result addChannelPriority(String channelPriority, int priorityFromValue){
        return updateChannelPriority(channelPriority, priorityFromValue, null);
    }

    public void deleteChannelPriority(String channelPriority) {
        final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                "/state/prioritySettings/"+channelPriority;

        final HttpDelete httpDelete = new HttpDelete(fullChannelEndpoint);
        ExecutionResult executionResult;
        try {
            final HttpResponse deleteResponse = new DefaultHttpClient(params).execute(httpDelete);

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
            throw new ChannelClientException("Unknown exception occurred when trying to update the message:", e, ChannelClientException.ExceptionType.UNKNOWN);
        }

        if(!executionResult.isSuccess()){
            throw new ChannelClientException("Could not delete priority", ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result getChannelPriority(String channelPriority) {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/state/prioritySettings/" + channelPriority;
            final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
            httpGet.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
            DefaultHttpClient client = new DefaultHttpClient(params);
            final HttpResponse getResponse = client.execute(httpGet);

            return new ResultsFactory().retrievalResultFromHttpGetResponse(getResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to get channel priority:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result updateBatchStrategy(BatchStrategy batchStrategy) {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/state/batchStrategy";

            final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
            DefaultHttpClient client = new DefaultHttpClient(params);
            final HttpResponse response = client.execute(httpGet);

            final HttpPut httpPut = new HttpPut(fullChannelEndpoint);
            httpPut.setEntity(createStringEntity(batchStrategy.name()));
            httpPut.setHeader(HttpHeaders.IF_MATCH, response.getFirstHeader(HttpHeaders.ETAG).getValue());

            DefaultHttpClient conditionalPutClient = new DefaultHttpClient(params);
            final HttpResponse conditionalPutResponse = conditionalPutClient.execute(httpPut);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(conditionalPutResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to update batch strategy:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result updateSequenceStrategy(SequenceStrategy sequenceStrategy) {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/state/sequenceStrategy";

            final HttpGet httpGet = new HttpGet(fullChannelEndpoint);
            DefaultHttpClient client = new DefaultHttpClient(params);
            final HttpResponse response = client.execute(httpGet);

            final HttpPut httpPut = new HttpPut(fullChannelEndpoint);
            httpPut.setEntity(createStringEntity(sequenceStrategy.name()));
            httpPut.setHeader(HttpHeaders.IF_MATCH, response.getFirstHeader(HttpHeaders.ETAG).getValue());

            DefaultHttpClient conditionalPutClient = new DefaultHttpClient(params);
            final HttpResponse conditionalPutResponse = conditionalPutClient.execute(httpPut);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(conditionalPutResponse);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to update sequence strategy:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result registerMessageListener(String listenerId, ReturnAddress returnAddress, String batchId, String priority) {
        try {
            if (batchId != null && priority != null) {
                throw new IllegalArgumentException("BatchId and priority cannot both be set. Just set one or the other or none.");
            }
            String qualifier = "";
            if (batchId != null) {
                qualifier = "/batch/" + batchId;
            }
            if (priority != null) {
                qualifier = "/priority/" + priority;
            }
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/registration" + qualifier + "/messagelisteners/" + listenerId;

            final HttpPut httpPut = new HttpPut(fullChannelEndpoint);

            httpPut.setHeader(CustomHeaders.RETURN_ADDRESSES.getName(), returnAddress.format());

            DefaultHttpClient putRequest = new DefaultHttpClient(params);
            return new ResultsFactory().conditionalPutResultFromHttpPutResponse(putRequest.execute(httpPut));
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to register message listener:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public Result unRegisterMessageListener(String listenerId, String batchId, String priority) {
        try {
            if (batchId != null && priority != null) {
                throw new IllegalArgumentException("BatchId and priority cannot both be set. Just set one or the other or none.");
            }
            String qualifier = "";
            if (batchId != null) {
                qualifier = "/batch/" + batchId;
            }
            if (priority != null) {
                qualifier = "/priority/" + priority;
            }
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/registration" + qualifier + "/messagelisteners/" + listenerId;

            final HttpDelete httpDelete = new HttpDelete(fullChannelEndpoint);

            DefaultHttpClient deleteRequest = new DefaultHttpClient(params);
            return new ResultsFactory().executionResultFromHttpDeleteResponse(deleteRequest.execute(httpDelete));
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to unregister message listener:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public void purgeChannel() {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/purge";

            final HttpPost httpPost = new HttpPost(fullChannelEndpoint);

            DefaultHttpClient postRequest = new DefaultHttpClient(params);
            postRequest.execute(httpPost);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to purge channel:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public void takeSnapshot() {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/snapshots";

            final HttpPost httpPost = new HttpPost(fullChannelEndpoint);

            DefaultHttpClient postRequest = new DefaultHttpClient(params);
            postRequest.execute(httpPost);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to take a snapshot:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }

    public void restoreFromSnapshot(String snapshotId) {
        try {
            final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint) +
                    "/restore/" + snapshotId;

            final HttpPost httpPost = new HttpPost(fullChannelEndpoint);

            DefaultHttpClient postRequest = new DefaultHttpClient(params);
            postRequest.execute(httpPost);
        }
        catch (HttpHostConnectException e) {
            throw new ChannelClientException("Exception connecting to server. " +
                    "Ensure server is running and configured using the right ip address and port.", e,
                    ChannelClientException.ExceptionType.CONNECTION);
        }
        catch (ClientProtocolException e) {
            throw new ChannelClientException("Exception communicating with server.", e,
                    ChannelClientException.ExceptionType.TRANSPORT_PROTOCOL);
        }
        catch (Exception e) {
            throw new ChannelClientException("Unknown exception occurred when trying to restore a snapshot:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
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

    private StringEntity createStringEntity(String messageBody) {
        try {
            return new StringEntity(messageBody);
        }
        catch (UnsupportedEncodingException e) {
            throw new ChannelClientException("UnsupportedEncodingException creating http body :",e,
                                ChannelClientException.ExceptionType.CHARACTER_ENCODING);
        }
    }
}
