package com.restqueue.framework.client.messageproducers;

import com.restqueue.common.utils.URLUtils;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.results.CreationResult;
import com.restqueue.framework.client.results.ResultsFactory;
import com.restqueue.framework.service.server.AbstractServer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates the functionality that you need to send messages into a channel.<BR/><BR/>
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
 * Date: Jan 6, 2011
 * Time: 9:24:13 AM
 */
public class BasicMessageSender {
    private String channelEndpoint;
    private final Map<CustomHeaders, List<String>> headerMap = new HashMap<CustomHeaders, List<String>>();
    private String stringBody;
    private Object objectBody;
    private String serverIpAddress = "localhost";
    private Integer serverPort = AbstractServer.PORT;
    private MediaType asType;


    /**
     * This enables you to send a message.
     *
     * You must set either the stringBody (if you already have a json or xml message formatted) or the objectBody first before sending a message.
     * These can be set using setStringBodyAndType(String stringBody, MediaType asType) and setObjectBody(Object objectBody) respectively.
     *
     * You also must set the channel endpoint as well as the server IP address and port (if they vary from the defaults localhost and 9998).
     *
     * @return The result of the update giving you access to the http response code and error information
     */
    public CreationResult sendMessage() {

        Object messageBody=null;
        if(stringBody==null && objectBody==null){
            throw new IllegalArgumentException("String body and Object body cannot both be null.");
        }
        else{
            if(stringBody!=null){
                messageBody=stringBody;
            }
            if(objectBody!=null){
                messageBody=objectBody;
            }
        }

        if(channelEndpoint ==null){
            throw new IllegalArgumentException("The Channel Endpoint must be set.");
        }

        if(messageBody instanceof String && asType==null){
            throw new IllegalArgumentException("The type must be set when using a String body.");
        }

        final String fullUrl = URLUtils.renderFullUrlFromIpAddressAndPort(serverIpAddress, serverPort, channelEndpoint);
        final HttpPost httpPost = new HttpPost(fullUrl+"/entries");

        if(messageBody instanceof String){
            httpPost.setEntity(createStringEntity((String) messageBody));
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, asType.toString());
        }
        else{
            httpPost.setEntity(createStringEntity(new Serializer().toType(messageBody, MediaType.APPLICATION_JSON)));
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }

        for (Map.Entry<CustomHeaders, List<String>> entry : headerMap.entrySet()) {
            for (String headerValue : entry.getValue()) {
                httpPost.addHeader(entry.getKey().getName(), headerValue);
            }
        }

        final HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        final DefaultHttpClient client = new DefaultHttpClient(params);

        final HttpResponse response;
        try {
            response = client.execute(httpPost);
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
            throw new ChannelClientException("Unknown exception occurred when trying to send the message:", e, ChannelClientException.ExceptionType.UNKNOWN);
        }
        return new ResultsFactory().creationResultFromHttpPostResponse(response);
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
     * Set the channel to perform the operation on.
     * @param channelEndpoint The channel endpoint (eg. http://{serverip}:{serverport}/channels/1.0/{channelName})
     */
    public void setChannelEndpoint(String channelEndpoint) {
        this.channelEndpoint = channelEndpoint;
    }

    /**
     * This enables you to set the headers that are required for specific functionality (eg. delaying messages, batching them etc)
     * @param customHeaders The header type
     * @param values The header values to set.
     */
    public void addHeader(CustomHeaders customHeaders, List<String> values){
        if(headerMap.containsKey(customHeaders)){
            headerMap.get(customHeaders).addAll(values);
        }
        else{
            headerMap.put(customHeaders, values);
        }
    }

    /**
     * This is to set the body if you already have a serialized version of the body of the message. You will need to make
     * sure that the class of the message body is in the classpath of the server.
     * @param stringBody The serialized message body
     * @param asType The serialization format that the stringBody is using.
     */
    public void setStringBodyAndType(String stringBody, MediaType asType) {
        this.stringBody = stringBody;
        this.asType = asType;
    }

    /**
     * This is to set the body of the message which can be anything that extends Object.
     * @param objectBody The body
     */
    public void setObjectBody(Object objectBody) {
        this.objectBody = objectBody;
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
}
