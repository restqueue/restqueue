package com.restqueue.framework.client.channelmanagement;

import com.restqueue.common.utils.URLUtils;
import com.restqueue.framework.client.common.entryfields.ReturnAddress;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.results.ConditionalPutResult;
import com.restqueue.framework.client.results.ExecutionResult;
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
 * This class enables you to perform the channel administration functions easily. The simple pattern is: set the channel endpoint,
 * set the server IP address (if needed), set the server port (if needed) and start using the methods for the functionality
 * you need.
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

    /**
     * This returns the current max size that the messaging channel is configured with.
     *
     * @return The maximum size
     */
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

    /**
     * This allows you to change the max size for the channel.
     *
     * @param maxSize The new maximum size
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult updateChannelMaxSize(long maxSize) {
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

    /**
     * This allows you to update the next message sequence to control the order for a Sequencer type channel.
     *
     * @param nextMessageSequence The new sequence number
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult updateNextMessageSequence(long nextMessageSequence) {
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

    /**
     * This enables you to stop any more messages being added into the channel. This is useful when you need to clear out the
     * channel contents before a server is restarted.
     *
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult drain() {
        return updateChannelMaxSize(0);
    }

    /**
     * This steps the next message sequence for the channel forward by one.
     *
     * @param currentSequence The current value for the channel's nextMessageSequence
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult incrementNextMessageSequence(int currentSequence) {
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

    /**
     * This enables you to update the priority bands for the channel. The new value you provide will become the lowest value
     * that a message can have to qualify for this priority level. The next higher level above the priority level specified
     * will be the ceiling value below which a message priority must be to qualify for the specified level.
     *
     * For example, a priority level of 'medium' might be from 23 to 71 inclusive, and a level of high might be from 72 to 100.
     * To lower the priority band for 'medium' you would call this method with channelPriority='medium' and priorityFromValue=20.
     * This would change the bands to be medium:20-71 and high 72-100.
     *
     * To raise the priority band for 'medium' you would call this method with channelPriority='high' and priorityFromValue=80.
     * This would change the bands to be medium:20-79 and high 80-100.
     *
     * @param channelPriority The channel priority to change
     * @param priorityFromValue The lowest value for the priority band
     * @param eTag The current value
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult updateChannelPriority(String channelPriority, int priorityFromValue, String eTag) {
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

    /**
     * This enables you to create a new priority group. You just supply the new priority group name and its lowest band value
     * and the framework code adjusts any existing priority groups to suit.
     *
     * For example if you want to add a new band 'important' with existing bands of medium:23-71 and high:72-100, you could
     * call this method with channelPriority='important' and priorityFromValue=60. The result of this would be medium:23-59,
     * important:60-71 and high:72-100
     *
     * @param channelPriority The new priority
     * @param priorityFromValue The lower band level
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult addChannelPriority(String channelPriority, int priorityFromValue){
        return updateChannelPriority(channelPriority, priorityFromValue, null);
    }

    /**
     * This method provides the functionality to remove channel priorities. Just provide the name of the priority to delete
     * and (if successful) the higher boundary of the next lower priority will be adjusted to cover the newly deleted band.
     *
     * For example if you had medium:23-59, important:60-71 and high:72-100 and wanted to delete 'important' then you would be
     * left with medium:23-71 and high:72-100
     *
     * @param channelPriority The name of the channel priority to delete
     */
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

    /**
     * This method returns the lower value of the priority band specified.
     *
     * @param channelPriority The name of the priority
     * @return The result of the request giving you access to the http response code and error information. The integer value will be in the body.
     */
    public RetrievalResult getChannelPriority(String channelPriority) {
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

    /**
     * This enables you to switch to a different strategy for retrieval of batched messages. See BatchStrategy for details.
     *
     * @param batchStrategy The batch strategy to move to
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult updateBatchStrategy(BatchStrategy batchStrategy) {
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

    /**
     * This enables you to switch to a different strategy for retrieval of sequenced messages from a Sequencer channel.
     * See SequenceStrategy for details.
     *
     * @param sequenceStrategy The sequence strategy to move to
     * @return The result of the update giving you access to the http response code and error information
     */
    public ConditionalPutResult updateSequenceStrategy(SequenceStrategy sequenceStrategy) {
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

    /**
     * This will register a new message listener onto the channel. You can optionally set a batchId OR a priority
     * (but it is invalid to specify both).
     *
     * @param listenerId The unique identifier with which you identify the listener
     * @param returnAddress The return address specifying the method of contacting the listener and the address to use
     * @param batchId The optional batchID
     * @param priority The optional priority
     * @return The result of the registration giving you access to the http response code and error information
     */
    public ConditionalPutResult registerMessageListener(String listenerId, ReturnAddress returnAddress, String batchId, String priority) {
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

    /**
     * This will delete (unregister) a message listener from a channel. You can optionally set a batchId OR a priority
     * (but it is invalid to specify both).
     *
     * @param listenerId The unique identifier with which you identify the listener
     * @param batchId The optional batchID
     * @param priority The optional priority
     * @return The result of the un-registration giving you access to the http response code and error information
     */
    public ExecutionResult unRegisterMessageListener(String listenerId, String batchId, String priority) {
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

    /**
     * purgeChannel() triggers a snapshot to be taken and then completely clears out the channel.
     */
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

    /**
     * This will take a snapshot of the channel contents, state and message listener information and write it to disk.
     */
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

    /**
     * This enables a channel to be completely restored from a previously saved snapshot. You just provide the snapshot id
     * which is either selected fom the snapshot list via the web interface or by finding the correct snapshot on disk.
     *
     * @param snapshotId The snapshotId to restore (this is a timestamp in yyyyMMddhhmmss format)
     */
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

    /**
     * Set the channel to perform the operation on.
     * @param channelEndpoint The channel endpoint (eg. /channels/1.0/{channelName})
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

    private StringEntity createStringEntity(String messageBody) {
        try {
            return new StringEntity(messageBody);
        }
        catch (UnsupportedEncodingException e) {
            throw new ChannelClientException("UnsupportedEncodingException creating http body :",e,
                                ChannelClientException.ExceptionType.CHARACTER_ENCODING);
        }
    }

    /**
     * This instructs the server to shutdown
     */
    public void stopServer(){
        final String fullChannelEndpoint = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, "/control/1.0/stopserver");

        final HttpPost httpPost = new HttpPost(fullChannelEndpoint);

        DefaultHttpClient postRequest = new DefaultHttpClient(params);
        try {
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
            throw new ChannelClientException("Unknown exception occurred when trying to stop the server:", e,
                    ChannelClientException.ExceptionType.UNKNOWN);
        }
    }
}
