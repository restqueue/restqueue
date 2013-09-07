package com.restqueue.framework.client.messageproducers;

import com.restqueue.common.utils.URLUtils;
import com.restqueue.framework.client.common.messageheaders.CustomHeaders;
import com.restqueue.framework.client.common.serializer.Serializer;
import com.restqueue.framework.client.exception.ChannelClientException;
import com.restqueue.framework.client.results.Result;
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

    public Result sendMessage() {

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

    public void setChannelEndpoint(String channelEndpoint) {
        this.channelEndpoint = channelEndpoint;
    }

    public void addHeader(CustomHeaders customHeaders, List<String> values){
        if(headerMap.containsKey(customHeaders)){
            headerMap.get(customHeaders).addAll(values);
        }
        else{
            headerMap.put(customHeaders, values);
        }
    }

    public void setStringBodyAndType(String stringBody, MediaType asType) {
        this.stringBody = stringBody;
        this.asType = asType;
    }

    public void setObjectBody(Object objectBody) {
        this.objectBody = objectBody;
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
}
